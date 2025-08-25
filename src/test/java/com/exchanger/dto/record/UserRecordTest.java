package com.exchanger.dto.record;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRecordTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testValidUserRecord() {
        Set<ConstraintViolation<UserRecord>> violations =
                getViolationsFromUserRecord("John", "Doe", "john.doe@example.com");

        assertTrue(violations.isEmpty());
    }

    @Test
    void testBlankFirstName() {
        Set<ConstraintViolation<UserRecord>> violations =
                getViolationsFromUserRecord("", "Doe", "john.doe@example.com");

        assertFalse(violations.isEmpty());
        assertEquals("First name cannot be blank", violations.iterator().next().getMessage());
    }

    @Test
    void testBlankLastName() {
        Set<ConstraintViolation<UserRecord>> violations =
                getViolationsFromUserRecord("John", "", "john.doe@example.com");

        assertFalse(violations.isEmpty());
        assertEquals("Last name cannot be blank", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidEmail() {
        Set<ConstraintViolation<UserRecord>> violations =
                getViolationsFromUserRecord("John", "Doe", "invalid-email");

        assertFalse(violations.isEmpty());
        assertEquals("Invalid email format", violations.iterator().next().getMessage());
    }

    @Test
    void testBlankEmail() {
        Set<ConstraintViolation<UserRecord>> violations =
                getViolationsFromUserRecord("John", "Doe", "");

        assertFalse(violations.isEmpty());
        assertEquals("Email cannot be blank", violations.iterator().next().getMessage());
    }

    private Set<ConstraintViolation<UserRecord>> getViolationsFromUserRecord
            (String firstName, String lastName, String email) {
        UserRecord userRecord = new UserRecord(firstName, lastName, email);
        return validator.validate(userRecord);
    }

}
