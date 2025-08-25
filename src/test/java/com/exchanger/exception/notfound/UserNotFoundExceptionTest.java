package com.exchanger.exception.notfound;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserNotFoundExceptionTest {

    @Test
    void testUserNotFoundException() {
        String message = "User Not Found";
        UserNotFoundException userNotFound = new UserNotFoundException(message);
        assertEquals(message, userNotFound.getMessage());
    }

}
