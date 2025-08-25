package com.exchanger.service.impl;

import com.exchanger.dto.*;
import com.exchanger.exception.NotEnoughMoneyException;
import com.exchanger.exception.notfound.TransactionNotFoundException;
import com.exchanger.exception.notfound.WalletNotFoundException;
import com.exchanger.mapper.TransactionMapper;
import com.exchanger.model.Rate;
import com.exchanger.model.Transaction;
import com.exchanger.model.User;
import com.exchanger.model.Wallet;
import com.exchanger.model.enums.Currency;
import com.exchanger.model.enums.TransactionStatus;
import com.exchanger.model.enums.TransactionType;
import com.exchanger.repository.TransactionRepository;
import com.exchanger.repository.WalletRepository;
import com.exchanger.service.CacheService;
import com.exchanger.service.RateService;
import com.exchanger.service.UserService;
import com.exchanger.service.WalletService;
import com.exchanger.telegram.TelegramBot;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final UserService userService;
    private final RateService rateService;
    private final CacheService cacheService;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final TelegramBot telegramBot;

    @Override
    public Wallet getWalletByUserAndCurrency(User user, Currency currency) {
        return walletRepository.findByUserAndCurrency(user, currency)
                .orElseThrow(() -> new WalletNotFoundException("Wallet with such data was not found"));
    }

    @Override
    public UUID putMoney(PutMoneyDto putMoneyDto) {
        Transaction transaction = transactionMapper.fromPutMoneyDto(putMoneyDto);

        User user = userService.findByEmail(transaction.getSender());
        Wallet wallet = getWalletByUserAndCurrency(user, transaction.getCurrencyTo());

        wallet.setBalance(wallet.getBalance().add(transaction.getAmountTo()));
        return saveTransaction(transaction);
    }

    private UUID saveTransaction(Transaction transaction) {
        transaction.setStatus(TransactionStatus.EXECUTED);
        return transactionRepository.save(transaction).getId();
    }

    @Override
    public UUID getMoney(GetMoneyDto getMoneyDto) {
        Transaction transaction = transactionMapper.fromGetMoneyDto(getMoneyDto);

        User user = userService.findByEmail(transaction.getReceiver());
        Wallet wallet = getWalletByUserAndCurrency(user, transaction.getCurrencyTo());
        validateBalance(transaction, wallet);

        wallet.setBalance(wallet.getBalance().subtract(transaction.getAmountTo()));
        return saveTransaction(transaction);
    }

    private void validateBalance(Transaction transaction, Wallet wallet) {
        BigDecimal balance = wallet.getBalance();
        BigDecimal amount = getAmountToValidate(transaction);

        if (balance.compareTo(amount) < 0) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);

            throw new NotEnoughMoneyException("Not enough funds in wallet");
        }
    }

    private BigDecimal getAmountToValidate(Transaction transaction) {
        TransactionType type = transaction.getType();
        if (type.equals(TransactionType.TRANSF) || type.equals(TransactionType.EXCH)) {
            return transaction.getAmountFrom();
        }
        return transaction.getAmountTo();
    }

    @Override
    public UUID exchange(ExchangeMoneyDto exchangeDto) {
        Transaction transaction = transactionMapper.fromExchangeDto(exchangeDto);

        User user = userService.findByEmail(transaction.getSender());
        Wallet sourceWallet = getWalletByUserAndCurrency(user, transaction.getCurrencyFrom());
        Wallet targetWallet = getWalletByUserAndCurrency(user, transaction.getCurrencyTo());

        validateBalance(transaction, sourceWallet);
        sourceWallet.setBalance(sourceWallet.getBalance().subtract(transaction.getAmountFrom()));

        BigDecimal convertedAmount = convertCurrency(transaction.getAmountFrom(), transaction.getCurrencyFrom(), transaction.getCurrencyTo());
        targetWallet.setBalance(targetWallet.getBalance().add(convertedAmount));
        transaction.setAmountTo(convertedAmount);

        return saveTransaction(transaction);
    }

    private BigDecimal convertCurrency(BigDecimal amount, Currency sourceCurrency, Currency targetCurrency) {
        BigDecimal exchangeRate = getExchangeRate(sourceCurrency, targetCurrency);
        return amount.multiply(exchangeRate);
    }

    private BigDecimal getExchangeRate(Currency sourceCurrency, Currency targetCurrency) {
        if(sourceCurrency.equals(targetCurrency)) {
            return BigDecimal.ONE;
        }
        Rate sourceRate = rateService.getRateByCurrency(sourceCurrency);
        Rate targetRate = rateService.getRateByCurrency(targetCurrency);

        BigDecimal buy = sourceRate.getBuy();
        BigDecimal sale = targetRate.getSale();
        return buy.divide(sale, 8, RoundingMode.HALF_DOWN);
    }

    @Override
    public UUID transfer(TransferMoneyDto transferDto) {
        Transaction transaction = transactionMapper.fromTransferDto(transferDto);

        User sender = userService.findByEmail(transaction.getSender());
        User receiver = userService.findByEmail(transaction.getReceiver());

        getWalletByUserAndCurrency(receiver, transaction.getCurrencyTo());
        Wallet walletSender = getWalletByUserAndCurrency(sender, transaction.getCurrencyTo());
        validateBalance(transaction, walletSender);

        String code = RandomStringUtils.secure().nextAlphanumeric(6);
        cacheService.addValueToCache(transferDto.getSender(), code);

        String message = "Code for approval: " + code;
        telegramBot.sendMessage(sender.getTelegramChatId(), message);

        return transactionRepository.save(transaction).getId();
    }

    @Override
    public UUID transferApprove(TransferApproveDto transferDto) {
        String senderEmail = transferDto.getEmail();
        User sender = userService.findByEmail(senderEmail);

        Transaction transaction = transactionRepository.findByIdAndSender(transferDto.getTransactionId(), senderEmail)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction for approval not found"));

        String otpCode = cacheService.getValueFromCache(senderEmail);
        validateOtpCode(transaction, otpCode, transferDto.getCode());

        User receiver = userService.findByEmail(transaction.getReceiver());
        Wallet walletSender = getWalletByUserAndCurrency(sender, transaction.getCurrencyTo());
        Wallet walletReceiver = getWalletByUserAndCurrency(receiver, transaction.getCurrencyTo());

        validateBalance(transaction, walletSender);
        walletSender.setBalance(walletSender.getBalance().subtract(transaction.getAmountFrom()));
        walletReceiver.setBalance(walletReceiver.getBalance().add(transaction.getAmountTo()));

        return saveTransaction(transaction);
    }

    private void validateOtpCode(Transaction transaction, String expected, String actual) {
        if(!expected.equals(actual)) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            
            throw new TransactionNotFoundException("Transaction code for approval not found");
        }
    }

}
