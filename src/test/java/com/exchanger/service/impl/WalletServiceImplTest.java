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
import com.exchanger.telegram.TelegramBot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private RateService rateService;

    @Mock
    private CacheService cacheService;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private TelegramBot telegramBot;

    @InjectMocks
    private WalletServiceImpl walletService;

    @Test
    void putMoney_shouldAddMoneyAndSaveTransaction() {
        PutMoneyDto dto = new PutMoneyDto();
        Transaction transaction = new Transaction();
        User user = new User();
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(100));
        transaction.setCurrencyTo(Currency.USD);
        transaction.setAmountTo(BigDecimal.valueOf(50));
        transaction.setSender("test@example.com");

        when(transactionMapper.fromPutMoneyDto(dto)).thenReturn(transaction);
        when(userService.findByEmail("test@example.com")).thenReturn(user);
        when(walletRepository.findByUserAndCurrency(user, Currency.USD)).thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> {
            Transaction t = i.getArgument(0);
            t.setId(UUID.randomUUID());
            return t;
        });

        UUID result = walletService.putMoney(dto);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(150), wallet.getBalance());
    }

    @Test
    void getWalletByUserAndCurrency_shouldThrow_whenWalletNotFound() {
        User user = new User();
        Currency currency = Currency.USD;

        when(walletRepository.findByUserAndCurrency(user, currency)).thenReturn(Optional.empty());

        WalletNotFoundException exception = assertThrows(
                WalletNotFoundException.class,
                () -> walletService.getWalletByUserAndCurrency(user, currency)
        );

        assertEquals("Wallet with such data was not found", exception.getMessage());
    }

    @Test
    void getMoney_shouldThrowException_whenNotEnoughBalance() {
        GetMoneyDto dto = new GetMoneyDto();
        Transaction transaction = new Transaction();
        User user = new User();
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(30));
        transaction.setAmountTo(BigDecimal.valueOf(50));
        transaction.setReceiver("user@example.com");
        transaction.setCurrencyTo(Currency.EUR);
        transaction.setType(TransactionType.GET);

        when(transactionMapper.fromGetMoneyDto(dto)).thenReturn(transaction);
        when(userService.findByEmail("user@example.com")).thenReturn(user);
        when(walletRepository.findByUserAndCurrency(user, Currency.EUR)).thenReturn(Optional.of(wallet));

        assertThrows(NotEnoughMoneyException.class, () -> walletService.getMoney(dto));

        verify(transactionRepository).save(argThat(t -> t.getStatus() == TransactionStatus.FAILED));
    }

    @Test
    void getMoney_shouldSubtractBalanceAndSaveTransaction() {
        GetMoneyDto dto = new GetMoneyDto();

        Transaction transaction = new Transaction();
        transaction.setReceiver("user@example.com");
        transaction.setCurrencyTo(Currency.USD);
        transaction.setAmountTo(BigDecimal.valueOf(40));
        transaction.setType(TransactionType.GET);

        User user = new User();
        user.setEmail("user@example.com");

        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(100));

        when(transactionMapper.fromGetMoneyDto(dto)).thenReturn(transaction);
        when(userService.findByEmail("user@example.com")).thenReturn(user);
        when(walletRepository.findByUserAndCurrency(user, Currency.USD)).thenReturn(Optional.of(wallet));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction t = invocation.getArgument(0);
            t.setId(UUID.randomUUID());
            return t;
        });

        UUID result = walletService.getMoney(dto);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(60), wallet.getBalance());
        verify(transactionRepository).save(argThat(t -> t.getStatus() == TransactionStatus.EXECUTED));
    }

    @Test
    void transferApprove_shouldSucceed_whenOtpIsCorrectAndBalanceSufficient() {
        UUID transactionId = UUID.randomUUID();
        TransferApproveDto dto = new TransferApproveDto();
        dto.setTransactionId(transactionId);
        dto.setEmail("alice@example.com");
        dto.setCode("123456");

        User sender = new User();
        sender.setEmail("alice@example.com");

        User receiver = new User();
        receiver.setEmail("bob@example.com");

        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setSender("alice@example.com");
        transaction.setReceiver("bob@example.com");
        transaction.setCurrencyTo(Currency.USD);
        transaction.setAmountFrom(BigDecimal.valueOf(50));
        transaction.setAmountTo(BigDecimal.valueOf(50));
        transaction.setType(TransactionType.TRANSF);

        Wallet senderWallet = new Wallet();
        senderWallet.setBalance(BigDecimal.valueOf(100));

        Wallet receiverWallet = new Wallet();
        receiverWallet.setBalance(BigDecimal.valueOf(0));

        when(userService.findByEmail("alice@example.com")).thenReturn(sender);
        when(transactionRepository.findByIdAndSender(transactionId, "alice@example.com")).thenReturn(Optional.of(transaction));
        when(cacheService.getValueFromCache("alice@example.com")).thenReturn("123456");
        when(userService.findByEmail("bob@example.com")).thenReturn(receiver);
        when(walletRepository.findByUserAndCurrency(sender, Currency.USD)).thenReturn(Optional.of(senderWallet));
        when(walletRepository.findByUserAndCurrency(receiver, Currency.USD)).thenReturn(Optional.of(receiverWallet));
        when(transactionRepository.save(transaction)).thenAnswer(i -> {
            transaction.setId(UUID.randomUUID());
            return transaction;
        });

        UUID result = walletService.transferApprove(dto);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(50), receiverWallet.getBalance());
        assertEquals(BigDecimal.valueOf(50), senderWallet.getBalance());
    }

    @Test
    void transferApprove_shouldThrow_whenOtpCodeIsWrong() {
        UUID transactionId = UUID.randomUUID();
        TransferApproveDto dto = new TransferApproveDto();
        dto.setTransactionId(transactionId);
        dto.setEmail("alice@example.com");
        dto.setCode("WRONG");

        User sender = new User();
        sender.setEmail("alice@example.com");

        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setSender("alice@example.com");
        transaction.setReceiver("bob@example.com");
        transaction.setCurrencyTo(Currency.USD);
        transaction.setAmountFrom(BigDecimal.TEN);
        transaction.setAmountTo(BigDecimal.TEN);
        transaction.setType(TransactionType.TRANSF);

        when(userService.findByEmail("alice@example.com")).thenReturn(sender);
        when(transactionRepository.findByIdAndSender(transactionId, "alice@example.com")).thenReturn(Optional.of(transaction));
        when(cacheService.getValueFromCache("alice@example.com")).thenReturn("123456");

        assertThrows(TransactionNotFoundException.class, () -> walletService.transferApprove(dto));

        verify(transactionRepository).save(argThat(t -> t.getStatus() == TransactionStatus.FAILED));
    }

    @Test
    void transferApprove_shouldThrow_whenTransactionNotFound() {
        UUID transactionId = UUID.randomUUID();
        String senderEmail = "alice@example.com";
        TransferApproveDto dto = new TransferApproveDto();
        dto.setTransactionId(transactionId);
        dto.setEmail("alice@example.com");
        dto.setCode("123456");

        User sender = new User();
        sender.setEmail(senderEmail);

        when(userService.findByEmail(senderEmail)).thenReturn(sender);
        when(transactionRepository.findByIdAndSender(transactionId, senderEmail)).thenReturn(Optional.empty());

        TransactionNotFoundException ex = assertThrows(
                TransactionNotFoundException.class,
                () -> walletService.transferApprove(dto)
        );

        assertEquals("Transaction for approval not found", ex.getMessage());
    }

    @Test
    void exchange_shouldExchangeMoneyAndSaveTransaction() {
        ExchangeMoneyDto dto = new ExchangeMoneyDto();
        Transaction transaction = new Transaction();
        transaction.setSender("user@example.com");
        transaction.setCurrencyFrom(Currency.USD);
        transaction.setCurrencyTo(Currency.EUR);
        transaction.setType(TransactionType.EXCH);
        transaction.setAmountFrom(BigDecimal.valueOf(100));

        User user = new User();
        Wallet sourceWallet = new Wallet();
        sourceWallet.setBalance(BigDecimal.valueOf(200));
        Wallet targetWallet = new Wallet();
        targetWallet.setBalance(BigDecimal.valueOf(50));

        Rate usdRate = new Rate();
        usdRate.setBuy(BigDecimal.valueOf(1));
        Rate eurRate = new Rate();
        eurRate.setSale(BigDecimal.valueOf(2));

        when(transactionMapper.fromExchangeDto(dto)).thenReturn(transaction);
        when(userService.findByEmail("user@example.com")).thenReturn(user);
        when(walletRepository.findByUserAndCurrency(user, Currency.USD)).thenReturn(Optional.of(sourceWallet));
        when(walletRepository.findByUserAndCurrency(user, Currency.EUR)).thenReturn(Optional.of(targetWallet));
        when(rateService.getRateByCurrency(Currency.USD)).thenReturn(usdRate);
        when(rateService.getRateByCurrency(Currency.EUR)).thenReturn(eurRate);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction t = invocation.getArgument(0);
            t.setId(UUID.randomUUID());
            return t;
        });

        UUID transactionId = walletService.exchange(dto);

        assertNotNull(transactionId);
        assertEquals(new BigDecimal("100"), sourceWallet.getBalance());
    }

    @Test
    void exchange_shouldNotExchangeMoney() {
        ExchangeMoneyDto dto = new ExchangeMoneyDto();
        Transaction transaction = new Transaction();
        transaction.setSender("user@example.com");
        transaction.setCurrencyFrom(Currency.USD);
        transaction.setCurrencyTo(Currency.USD);
        transaction.setType(TransactionType.EXCH);
        transaction.setAmountFrom(BigDecimal.valueOf(100));

        User user = new User();
        Wallet sourceWallet = new Wallet();
        sourceWallet.setBalance(BigDecimal.valueOf(200));
        Wallet targetWallet = new Wallet();
        targetWallet.setBalance(BigDecimal.valueOf(50));

        Rate usdRate = new Rate();
        usdRate.setBuy(BigDecimal.valueOf(1));
        Rate eurRate = new Rate();
        eurRate.setSale(BigDecimal.valueOf(2));

        when(transactionMapper.fromExchangeDto(dto)).thenReturn(transaction);
        when(userService.findByEmail("user@example.com")).thenReturn(user);
        when(walletRepository.findByUserAndCurrency(user, Currency.USD)).thenReturn(Optional.of(sourceWallet));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction t = invocation.getArgument(0);
            t.setId(UUID.randomUUID());
            return t;
        });

        UUID transactionId = walletService.exchange(dto);

        assertNotNull(transactionId);
        assertEquals(new BigDecimal("200"), sourceWallet.getBalance());
    }

    @Test
    void transfer_shouldGenerateOtpAndSaveTransaction() {
        TransferMoneyDto dto = new TransferMoneyDto();
        dto.setSender("alice@example.com");

        Transaction transaction = new Transaction();
        transaction.setSender("alice@example.com");
        transaction.setReceiver("bob@example.com");
        transaction.setCurrencyTo(Currency.USD);
        transaction.setAmountFrom(BigDecimal.valueOf(50));
        transaction.setType(TransactionType.TRANSF);

        User sender = new User();
        sender.setEmail("alice@example.com");
        sender.setTelegramChatId(123456L);

        User receiver = new User();
        receiver.setEmail("bob@example.com");

        Wallet senderWallet = new Wallet();
        senderWallet.setBalance(BigDecimal.valueOf(100));

        Wallet receiverWallet = new Wallet();

        when(transactionMapper.fromTransferDto(dto)).thenReturn(transaction);
        when(userService.findByEmail("alice@example.com")).thenReturn(sender);
        when(userService.findByEmail("bob@example.com")).thenReturn(receiver);
        when(walletRepository.findByUserAndCurrency(receiver, Currency.USD)).thenReturn(Optional.of(receiverWallet));
        when(walletRepository.findByUserAndCurrency(sender, Currency.USD)).thenReturn(Optional.of(senderWallet));
        when(transactionRepository.save(transaction)).thenAnswer(invocation -> {
            Transaction t = invocation.getArgument(0);
            t.setId(UUID.randomUUID());
            return t;
        });

        UUID result = walletService.transfer(dto);

        assertNotNull(result);
        verify(cacheService).addValueToCache(eq("alice@example.com"), anyString());
        verify(telegramBot).sendMessage(eq(123456L), contains("Code for approval"));
    }

}
