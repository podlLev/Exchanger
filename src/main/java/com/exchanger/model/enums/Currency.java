package com.exchanger.model.enums;

import com.exchanger.exception.notfound.CurrencyNotFoundException;

public enum Currency {

    UAH, USD, EUR;

    public static Currency getInstance(String currencyCode) {
        for (Currency currency : Currency.values()) {
            if (currency.name().equalsIgnoreCase(currencyCode)) {
                return currency;
            }
        }
        throw new CurrencyNotFoundException("Currency not found by currency code: " + currencyCode);
    }

}
