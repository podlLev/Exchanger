package com.exchanger.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

class NotUniqueDataExceptionTest {

    @Test
    void testNotUniqueDataException() {
        NotUniqueDataException notUniqueData = new NotUniqueDataException();
        assertNull(notUniqueData.getMessage());
    }

}
