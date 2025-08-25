package com.exchanger.controller;

import com.exchanger.model.Rate;
import com.exchanger.model.enums.Currency;
import com.exchanger.service.RateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private RateService rateService;

    @InjectMocks
    private RateController rateController;

    private Rate rate1;
    private Rate rate2;

    @BeforeEach
    void setUp() {
        rate1 = createRateWithIdAndCurrency("USD");
        rate2 = createRateWithIdAndCurrency("EUR");

        mockMvc = MockMvcBuilders.standaloneSetup(rateController).build();
    }

    private Rate createRateWithIdAndCurrency(String currency) {
        Rate rate = new Rate();
        rate.setId(UUID.randomUUID());
        rate.setCurrency(Currency.getInstance(currency));
        return rate;
    }

    @Test
    void getRates() throws Exception {
        List<Rate> rates = Arrays.asList(rate1, rate2);
        when(rateService.getRates()).thenReturn(rates);

        mockMvc.perform(get("/api/v1/rates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].currency").value("USD"))
                .andExpect(jsonPath("$[1].currency").value("EUR"));
    }

    @Test
    void getRateById() throws Exception {
        when(rateService.getRateById(rate1.getId())).thenReturn(rate1);

        mockMvc.perform(get("/api/v1/rates/" + rate1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(rate1.getId().toString()))
                .andExpect(jsonPath("$.currency").value("USD"));
    }

    @Test
    void getRateByCurrencyCode() throws Exception {
        String currencyCode = "EUR";
        when(rateService.getRateByCurrencyCode(currencyCode)).thenReturn(rate2);

        mockMvc.perform(get("/api/v1/rates/code/" + currencyCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency").value(currencyCode));
    }

}
