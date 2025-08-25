package com.exchanger.controller;

import com.exchanger.model.Rate;
import com.exchanger.service.RateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * @version 0.0.1
 */
@RestController
@RequestMapping("/api/v1/rates")
@RequiredArgsConstructor
@Slf4j
public class RateController {

    private final RateService rateService;

    @GetMapping
    public List<Rate> getRates() {
        log.info("Run method RateController.getRates");
        return rateService.getRates();
    }

    @GetMapping("/{id}")
    public Rate getRateById(@PathVariable UUID id) {
        log.info("Run method RateController.getRateById");
        return rateService.getRateById(id);
    }

    @GetMapping("/code/{currencyCode}")
    public Rate getRateByCurrencyCode(@PathVariable String currencyCode) {
        log.info("Run method RateController.getRateByCurrencyCode");
        return rateService.getRateByCurrencyCode(currencyCode);
    }

}
