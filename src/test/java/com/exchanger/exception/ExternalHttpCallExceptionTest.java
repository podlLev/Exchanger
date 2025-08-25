package com.exchanger.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExternalHttpCallExceptionTest {

    @Test
    void testExternalHttpCallException() {
        String message = "Failed to call external HTTP service";
        ExternalHttpCallException exception = new ExternalHttpCallException(message);
        assertEquals(message, exception.getMessage());
    }

}
