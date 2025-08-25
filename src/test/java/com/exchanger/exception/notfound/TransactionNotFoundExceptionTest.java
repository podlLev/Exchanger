package com.exchanger.exception.notfound;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionNotFoundExceptionTest {

    @Test
    void testTransactionNotFoundException() {
        String message = "Transaction Not Found";
        TransactionNotFoundException transactionNotFound = new TransactionNotFoundException(message);
        assertEquals(message, transactionNotFound.getMessage());
    }

}
