package com.exchanger.exception.notfound;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RateNotFoundExceptionTest {

    @Test
    void testRateNotFoundException() {
        String message = "Rate Not Found";
        RateNotFoundException rateNotFound = new RateNotFoundException(message);
        assertEquals(message, rateNotFound.getMessage());
    }

}
