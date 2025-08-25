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

class TransferMoneyDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testValidTransferMoneyDto() {
        Set<ConstraintViolation<TransferMoneyDto>> violations =
                getViolationsFromTransferMoneyDto("john.doe@example.com", "john.doe@example.com");

        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidSenderEmail() {
        Set<ConstraintViolation<TransferMoneyDto>> violations =
                getViolationsFromTransferMoneyDto("invalid-email", "john.doe@example.com");

        assertFalse(violations.isEmpty());
        assertEquals("Invalid email format", violations.iterator().next().getMessage());
    }

    @Test
    void testBlankSender() {
        Set<ConstraintViolation<TransferMoneyDto>> violations =
                getViolationsFromTransferMoneyDto("", "john.doe@example.com");

        assertFalse(violations.isEmpty());
        assertEquals("Email cannot be blank", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidReceiverEmail() {
        Set<ConstraintViolation<TransferMoneyDto>> violations =
                getViolationsFromTransferMoneyDto("john.doe@example.com", "invalid-email");

        assertFalse(violations.isEmpty());
        assertEquals("Invalid email format", violations.iterator().next().getMessage());
    }

    @Test
    void testBlankReceiver() {
        Set<ConstraintViolation<TransferMoneyDto>> violations =
                getViolationsFromTransferMoneyDto("john.doe@example.com", "");

        assertFalse(violations.isEmpty());
        assertEquals("Email cannot be blank", violations.iterator().next().getMessage());
    }

    private Set<ConstraintViolation<TransferMoneyDto>> getViolationsFromTransferMoneyDto
            (String sender, String receiver) {

        TransferMoneyDto transferMoneyDto = new TransferMoneyDto();
        transferMoneyDto.setSender(sender);
        transferMoneyDto.setReceiver(receiver);
        transferMoneyDto.setAmount(BigDecimal.valueOf(1));
        transferMoneyDto.setCurrency("USD");
        return validator.validate(transferMoneyDto);
    }

}
