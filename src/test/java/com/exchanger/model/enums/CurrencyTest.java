package com.exchanger.model.enums;

import com.exchanger.exception.notfound.CurrencyNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CurrencyTest {

    @Test
    void getInstance() {
        assertEquals(Currency.USD, Currency.getInstance("USD"));
        assertEquals(Currency.EUR, Currency.getInstance("EUR"));
        assertEquals(Currency.UAH, Currency.getInstance("UAH"));
    }

    @Test
    void getInstance_ignoringCase() {
        assertEquals(Currency.USD, Currency.getInstance("Usd"));
        assertEquals(Currency.EUR, Currency.getInstance("eUr"));
        assertEquals(Currency.UAH, Currency.getInstance("uaH"));
    }

    @Test
    void getInstance_invalidCurrencyCode() {
        String invalidCurrencyCode = "ABC";

        CurrencyNotFoundException exception = assertThrows(CurrencyNotFoundException.class,
                () -> Currency.getInstance(invalidCurrencyCode));

        String expected = "Currency not found by currency code: " + invalidCurrencyCode;
        assertEquals(expected, exception.getMessage());
    }

}
