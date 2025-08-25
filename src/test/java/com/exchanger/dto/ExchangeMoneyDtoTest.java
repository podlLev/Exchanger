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

class ExchangeMoneyDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testValidExchangeMoneyDto() {
        Set<ConstraintViolation<ExchangeMoneyDto>> violations =
                getViolationsFromExchangeMoneyDto("john.doe@example.com", "usd");

        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidEmail() {
        Set<ConstraintViolation<ExchangeMoneyDto>> violations =
                getViolationsFromExchangeMoneyDto("invalid-email", "usd");

        assertFalse(violations.isEmpty());
        assertEquals("Invalid email format", violations.iterator().next().getMessage());
    }

    @Test
    void testBlankEmail() {
        Set<ConstraintViolation<ExchangeMoneyDto>> violations =
                getViolationsFromExchangeMoneyDto("", "usd");

        assertFalse(violations.isEmpty());
        assertEquals("User cannot be blank", violations.iterator().next().getMessage());
    }

    @Test
    void testBlankCurrency() {
        Set<ConstraintViolation<ExchangeMoneyDto>> violations =
                getViolationsFromExchangeMoneyDto("john.doe@example.com", "");

        assertFalse(violations.isEmpty());
        assertEquals("Source currency cannot be blank", violations.iterator().next().getMessage());
    }

    private Set<ConstraintViolation<ExchangeMoneyDto>> getViolationsFromExchangeMoneyDto
            (String userEmail, String sourceCurrency) {

        ExchangeMoneyDto exchangeMoneyDto = new ExchangeMoneyDto();
        exchangeMoneyDto.setUser(userEmail);
        exchangeMoneyDto.setSourceCurrency(sourceCurrency);
        exchangeMoneyDto.setAmount(BigDecimal.valueOf(1));
        exchangeMoneyDto.setCurrency("USD");
        return validator.validate(exchangeMoneyDto);
    }

}
