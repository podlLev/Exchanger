package com.exchanger.exception.notfound;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CurrencyNotFoundExceptionTest {

    @Test
    void testCurrencyNotFoundException() {
        String message = "Currency Not Found";
        CurrencyNotFoundException currencyNotFound = new CurrencyNotFoundException(message);
        assertEquals(message, currencyNotFound.getMessage());
    }

}
