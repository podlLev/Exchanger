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

class PutMoneyDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testValidPutMoneyDto() {
        Set<ConstraintViolation<PutMoneyDto>> violations =
                getViolationsFromPutMoneyDto("john.doe@example.com");

        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidEmail() {
        Set<ConstraintViolation<PutMoneyDto>> violations =
                getViolationsFromPutMoneyDto("invalid-email");

        assertFalse(violations.isEmpty());
        assertEquals("Invalid email format", violations.iterator().next().getMessage());
    }

    @Test
    void testBlankSender() {
        Set<ConstraintViolation<PutMoneyDto>> violations =
                getViolationsFromPutMoneyDto("");

        assertFalse(violations.isEmpty());
        assertEquals("Sender cannot be blank", violations.iterator().next().getMessage());
    }

    private Set<ConstraintViolation<PutMoneyDto>> getViolationsFromPutMoneyDto(String sender) {
        PutMoneyDto putMoneyDto = new PutMoneyDto();
        putMoneyDto.setSender(sender);
        putMoneyDto.setAmount(BigDecimal.valueOf(1));
        putMoneyDto.setCurrency("USD");
        return validator.validate(putMoneyDto);
    }

}
