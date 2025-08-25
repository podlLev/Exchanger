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

class GetMoneyDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testValidGetMoneyDto() {
        Set<ConstraintViolation<GetMoneyDto>> violations =
                getViolationsFromGetMoneyDto("john.doe@example.com");

        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidEmail() {
        Set<ConstraintViolation<GetMoneyDto>> violations =
                getViolationsFromGetMoneyDto("invalid-email");

        assertFalse(violations.isEmpty());
        assertEquals("Invalid email format", violations.iterator().next().getMessage());
    }

    @Test
    void testBlankReceiver() {
        Set<ConstraintViolation<GetMoneyDto>> violations =
                getViolationsFromGetMoneyDto("");

        assertFalse(violations.isEmpty());
        assertEquals("Receiver cannot be blank", violations.iterator().next().getMessage());
    }

    private Set<ConstraintViolation<GetMoneyDto>> getViolationsFromGetMoneyDto(String receiver) {
        GetMoneyDto getMoneyDto = new GetMoneyDto();
        getMoneyDto.setReceiver(receiver);
        getMoneyDto.setAmount(BigDecimal.valueOf(1));
        getMoneyDto.setCurrency("USD");
        return validator.validate(getMoneyDto);
    }

}
