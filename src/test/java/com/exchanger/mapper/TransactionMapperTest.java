package com.exchanger.mapper;

import com.exchanger.dto.*;
import com.exchanger.model.Transaction;
import com.exchanger.model.enums.Currency;
import com.exchanger.model.enums.TransactionStatus;
import com.exchanger.model.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TransactionMapperTest {

    private TransactionMapper transactionMapper;

    @BeforeEach
    void setUp() {
        transactionMapper = Mappers.getMapper(TransactionMapper.class);
    }

    @Test
    void fromPutMoneyDto() {
        PutMoneyDto dto = new PutMoneyDto();
        dto.setAmount(new BigDecimal("100.00"));
        dto.setCurrency("USD");

        Transaction transaction = transactionMapper.fromPutMoneyDto(dto);

        assertNotNull(transaction);
        assertEquals(TransactionType.PUT, transaction.getType());
        assertEquals(dto.getAmount(), transaction.getAmountTo());
        assertEquals(Currency.getInstance(dto.getCurrency()), transaction.getCurrencyTo());
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());
    }

    @Test
    void fromPutMoneyDto_empty() {
        Transaction transaction = transactionMapper.fromPutMoneyDto(null);
        assertNull(transaction);
    }

    @Test
    void fromGetMoneyDto() {
        GetMoneyDto dto = new GetMoneyDto();
        dto.setAmount(new BigDecimal("50.00"));
        dto.setCurrency("EUR");

        Transaction transaction = transactionMapper.fromGetMoneyDto(dto);

        assertNotNull(transaction);
        assertEquals(TransactionType.GET, transaction.getType());
        assertEquals(dto.getAmount(), transaction.getAmountTo());
        assertEquals(Currency.getInstance(dto.getCurrency()), transaction.getCurrencyTo());
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());
    }

    @Test
    void fromGetMoneyDto_empty() {
        Transaction transaction = transactionMapper.fromGetMoneyDto(null);
        assertNull(transaction);
    }

    @Test
    void fromExchangeDto() {
        ExchangeMoneyDto dto = new ExchangeMoneyDto();
        dto.setAmount(new BigDecimal("200.00"));
        dto.setSourceCurrency("EUR");
        dto.setCurrency("USD");
        dto.setUser("john.doe@gmail.com");

        Transaction transaction = transactionMapper.fromExchangeDto(dto);

        assertNotNull(transaction);
        assertEquals(TransactionType.EXCH, transaction.getType());
        assertEquals(dto.getUser(), transaction.getSender());
        assertEquals(dto.getUser(), transaction.getReceiver());
        assertEquals(dto.getAmount(), transaction.getAmountFrom());
        assertEquals(Currency.getInstance(dto.getSourceCurrency()), transaction.getCurrencyFrom());
        assertEquals(Currency.getInstance(dto.getCurrency()), transaction.getCurrencyTo());
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());
    }

    @Test
    void fromExchangeDto_empty() {
        Transaction transaction = transactionMapper.fromExchangeDto(null);
        assertNull(transaction);
    }

    @Test
    void fromTransferDto() {
        TransferMoneyDto dto = new TransferMoneyDto();
        dto.setSender("john.doe1@gmail.com");
        dto.setAmount(new BigDecimal("500.00"));
        dto.setReceiver("john.doe2@gmail.com");
        dto.setCurrency("USD");

        Transaction transaction = transactionMapper.fromTransferDto(dto);

        assertNotNull(transaction);
        assertEquals(TransactionType.TRANSF, transaction.getType());
        assertEquals(dto.getSender(), transaction.getSender());
        assertEquals(dto.getReceiver(), transaction.getReceiver());
        assertEquals(dto.getAmount(), transaction.getAmountFrom());
        assertEquals(dto.getAmount(), transaction.getAmountTo());
        assertEquals(Currency.getInstance(dto.getCurrency()), transaction.getCurrencyFrom());
        assertEquals(Currency.getInstance(dto.getCurrency()), transaction.getCurrencyTo());
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());
    }

    @Test
    void fromTransferDto_empty() {
        Transaction transaction = transactionMapper.fromTransferDto(null);
        assertNull(transaction);
    }

    @Test
    void mapCommonFields() {
        MoneyTransactionDto dto = new MoneyTransactionDto();
        dto.setAmount(new BigDecimal("300.00"));
        dto.setCurrency("USD");

        Transaction transaction = new Transaction();
        transactionMapper.mapCommonFields(dto, transaction);

        assertEquals(dto.getAmount(), transaction.getAmountTo());
        assertEquals(Currency.getInstance(dto.getCurrency()), transaction.getCurrencyTo());
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());
    }

    @Test
    void mapCurrencyField() {
        String currencyCode = "USD";
        Currency currency = transactionMapper.mapCurrencyField(currencyCode);

        assertNotNull(currency);
        assertEquals(Currency.getInstance(currencyCode), currency);
    }

}
