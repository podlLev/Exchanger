package com.exchanger.mapper;

import com.exchanger.dto.*;
import com.exchanger.model.Transaction;
import com.exchanger.model.enums.Currency;
import com.exchanger.model.enums.TransactionStatus;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "type", expression = "java(com.exchanger.model.enums.TransactionType.PUT)")
    Transaction fromPutMoneyDto(PutMoneyDto transaction);

    @Mapping(target = "type", expression = "java(com.exchanger.model.enums.TransactionType.GET)")
    Transaction fromGetMoneyDto(GetMoneyDto transaction);

    @Mapping(target = "sender", source = "user")
    @Mapping(target = "receiver", source = "user")
    @Mapping(target = "amountFrom", source = "amount")
    @Mapping(target = "currencyFrom", expression = "java(mapCurrencyField(transaction.getSourceCurrency()))")
    @Mapping(target = "type", expression = "java(com.exchanger.model.enums.TransactionType.EXCH)")
    Transaction fromExchangeDto(ExchangeMoneyDto transaction);

    @Mapping(target = "amountFrom", source = "amount")
    @Mapping(target = "currencyFrom", expression = "java(mapCurrencyField(transaction.getCurrency()))")
    @Mapping(target = "type", expression = "java(com.exchanger.model.enums.TransactionType.TRANSF)")
    Transaction fromTransferDto(TransferMoneyDto transaction);

    @BeforeMapping
    default void mapCommonFields(MoneyTransactionDto transaction, @MappingTarget Transaction transactionEntity) {
        transactionEntity.setAmountTo(transaction.getAmount());
        Currency currency = mapCurrencyField(transaction.getCurrency());
        transactionEntity.setCurrencyTo(currency);
        transactionEntity.setStatus(TransactionStatus.PENDING);
    }

    default Currency mapCurrencyField(String currency) {
        return Currency.getInstance(currency);
    }

}
