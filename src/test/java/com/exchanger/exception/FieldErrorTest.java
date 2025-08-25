package com.exchanger.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FieldErrorTest {

    private FieldError fieldError;

    @BeforeEach
    void setUp() {
        fieldError = new FieldError("email", "Email should not be blank");
    }

    @Test
    void field() {
        assertEquals("email", fieldError.field());
    }

    @Test
    void message() {
        assertEquals("Email should not be blank", fieldError.message());
    }

}
