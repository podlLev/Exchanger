package com.exchanger.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotEnoughMoneyExceptionTest {

    @Test
    void testNotEnoughMoneyException() {
        String message = "Not Enough Money";
        NotEnoughMoneyException exception = new NotEnoughMoneyException(message);
        assertEquals(message, exception.getMessage());
    }

}
