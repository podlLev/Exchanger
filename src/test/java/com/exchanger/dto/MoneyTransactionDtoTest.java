package com.exchanger.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoneyTransactionDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testValidMoneyTransactionDto() {
        Set<ConstraintViolation<MoneyTransactionDto>> violations =
                getViolationsFromMoneyTransactionDto("usd", BigDecimal.valueOf(100));

        assertTrue(violations.isEmpty());
    }

    @Test
    void testBlankCurrency() {
        Set<ConstraintViolation<MoneyTransactionDto>> violations =
                getViolationsFromMoneyTransactionDto("", BigDecimal.valueOf(100));

        assertFalse(violations.isEmpty());
        assertEquals("Currency cannot be blank", violations.iterator().next().getMessage());
    }

    @Test
    void testBlankAmount() {
        Set<ConstraintViolation<MoneyTransactionDto>> violations =
                getViolationsFromMoneyTransactionDto("usd", null);

        assertFalse(violations.isEmpty());
        assertEquals("Amount cannot be null", violations.iterator().next().getMessage());
    }

    @Test
    void testNegativeAmount() {
        Set<ConstraintViolation<MoneyTransactionDto>> violations =
                getViolationsFromMoneyTransactionDto("usd", BigDecimal.valueOf(-1));

        assertFalse(violations.isEmpty());
        assertEquals("Amount must be greater than 0", violations.iterator().next().getMessage());
    }

    private Set<ConstraintViolation<MoneyTransactionDto>> getViolationsFromMoneyTransactionDto
            (String currency, BigDecimal amount) {

        MoneyTransactionDto moneyTransactionDto = new MoneyTransactionDto();
        moneyTransactionDto.setCurrency(currency);
        moneyTransactionDto.setAmount(amount);
        return validator.validate(moneyTransactionDto);
    }

}
