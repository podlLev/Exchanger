package com.exchanger.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransferApproveDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testValidTransferApproveDto() {
        Set<ConstraintViolation<TransferApproveDto>> violations =
                getViolationsFromTransferApproveDto(UUID.randomUUID(),"john.doe@example.com", "CODE");

        assertTrue(violations.isEmpty());
    }

    @Test
    void testNullTransactionId() {
        Set<ConstraintViolation<TransferApproveDto>> violations =
                getViolationsFromTransferApproveDto(null,"john.doe@example.com", "CODE");

        assertFalse(violations.isEmpty());
        assertEquals("Transaction ID cannot be null", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidEmail() {
        Set<ConstraintViolation<TransferApproveDto>> violations =
                getViolationsFromTransferApproveDto(UUID.randomUUID(),"invalid-email", "CODE");

        assertFalse(violations.isEmpty());
        assertEquals("Invalid email format", violations.iterator().next().getMessage());
    }

    @Test
    void testBlankEmail() {
        Set<ConstraintViolation<TransferApproveDto>> violations =
                getViolationsFromTransferApproveDto(UUID.randomUUID(),"", "CODE");

        assertFalse(violations.isEmpty());
        assertEquals("Email cannot be blank", violations.iterator().next().getMessage());
    }

    @Test
    void testBlankCode() {
        Set<ConstraintViolation<TransferApproveDto>> violations =
                getViolationsFromTransferApproveDto(UUID.randomUUID(),"john.doe@example.com", "");

        assertFalse(violations.isEmpty());
        assertEquals("Code cannot be blank", violations.iterator().next().getMessage());
    }

    private Set<ConstraintViolation<TransferApproveDto>> getViolationsFromTransferApproveDto
            (UUID transactionId, String email, String code) {

        TransferApproveDto transferApproveDto = new TransferApproveDto();
        transferApproveDto.setTransactionId(transactionId);
        transferApproveDto.setEmail(email);
        transferApproveDto.setCode(code);
        return validator.validate(transferApproveDto);
    }

}
