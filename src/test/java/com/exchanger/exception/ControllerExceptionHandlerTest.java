package com.exchanger.exception;

import com.exchanger.exception.notfound.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ControllerExceptionHandlerTest {

    private ControllerExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ControllerExceptionHandler();
    }

    @Test
    void handleInvalidTopUpTypeException() {
        Exception ex = new Exception("Invalid top-up type");
        ResponseEntity<?> response = handler.handleInvalidTopUpTypeException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid top-up type", response.getBody());
    }

    @Test
    void handleNotFoundException() {
        NotFoundException ex = new NotFoundException("Resource not found");
        ResponseEntity<?> response = handler.handleNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Resource not found", response.getBody());
    }

    @Test
    void handleNonUniqueException() {
        NotUniqueDataException ex = new NotUniqueDataException();
        ResponseEntity<?> response = handler.handleNonUniqueException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("You could not create user. Non unique values", response.getBody());
    }

    @Test
    void handleMethodArgumentNotValidException() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("objectName", "field1", "error1")
        ));

        ResponseEntity<?> response = handler.handleMethodArgumentNotValidException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(List.class, response.getBody());
    }

    @Test
    void handleInternalServerError() {
        SQLException ex = new SQLException("Database error");
        ResponseEntity<?> response = handler.handleInternalServerError(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Database error", response.getBody());
    }

}
