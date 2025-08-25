package com.exchanger.dto;

import com.exchanger.model.enums.UserStatus;
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

class UserDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testValidUserDto() {
        Set<ConstraintViolation<UserDto>> violations =
                getViolationsFromUserDto(UUID.randomUUID(), "John Doe","john.doe@example.com", UserStatus.ACTIVE);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testNullId() {
        Set<ConstraintViolation<UserDto>> violations =
                getViolationsFromUserDto(null, "John Doe","john.doe@example.com", UserStatus.ACTIVE);

        assertFalse(violations.isEmpty());
        assertEquals("ID cannot be null", violations.iterator().next().getMessage());
    }

    @Test
    void testBlankUsername() {
        Set<ConstraintViolation<UserDto>> violations =
                getViolationsFromUserDto(UUID.randomUUID(), "","john.doe@example.com", UserStatus.ACTIVE);

        assertFalse(violations.isEmpty());
        assertEquals("Username cannot be blank", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidEmail() {
        Set<ConstraintViolation<UserDto>> violations =
                getViolationsFromUserDto(UUID.randomUUID(),"John Doe","invalid-email", UserStatus.ACTIVE);

        assertFalse(violations.isEmpty());
        assertEquals("Invalid email format", violations.iterator().next().getMessage());
    }

    @Test
    void testBlankEmail() {
        Set<ConstraintViolation<UserDto>> violations =
                getViolationsFromUserDto(UUID.randomUUID(),"John Doe","", UserStatus.ACTIVE);

        assertFalse(violations.isEmpty());
        assertEquals("Email cannot be blank", violations.iterator().next().getMessage());
    }

    @Test
    void testNullStatus() {
        Set<ConstraintViolation<UserDto>> violations =
                getViolationsFromUserDto(UUID.randomUUID(),"John Doe","john.doe@example.com", null);

        assertFalse(violations.isEmpty());
        assertEquals("Status cannot be null", violations.iterator().next().getMessage());
    }

    private Set<ConstraintViolation<UserDto>> getViolationsFromUserDto
            (UUID id, String username, String email, UserStatus status) {

        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setUsername(username);
        userDto.setEmail(email);
        userDto.setStatus(status);
        return validator.validate(userDto);
    }

}
