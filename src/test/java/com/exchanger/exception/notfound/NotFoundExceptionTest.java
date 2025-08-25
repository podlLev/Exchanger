package com.exchanger.exception.notfound;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotFoundExceptionTest {

    @Test
    void testNotFoundException() {
        String message = "Not Found";
        NotFoundException notFound = new NotFoundException(message);
        assertEquals(message, notFound.getMessage());
    }

}
