package com.exchanger.service;

import com.exchanger.model.Rate;
import com.exchanger.model.enums.Currency;

import java.util.List;
import java.util.UUID;

public interface RateService {

    List<Rate> getRates();
    Rate getRateById(UUID id);
    Rate getRateByCurrency(Currency currency);
    Rate getRateByCurrencyCode(String currencyCode);
    void getRatesFromExternalSource();

}
