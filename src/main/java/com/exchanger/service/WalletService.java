package com.exchanger.service;

import com.exchanger.dto.*;
import com.exchanger.model.User;
import com.exchanger.model.Wallet;
import com.exchanger.model.enums.Currency;

import java.util.UUID;

public interface WalletService {

    Wallet getWalletByUserAndCurrency(User user, Currency currency);
    UUID putMoney(PutMoneyDto putMoneyDto);
    UUID getMoney(GetMoneyDto getMoneyDto);
    UUID exchange(ExchangeMoneyDto exchangeDto);
    UUID transfer(TransferMoneyDto transferDto);
    UUID transferApprove(TransferApproveDto transferDto);

}
