package com.exchanger.service.impl;

import com.exchanger.exception.ExternalHttpCallException;
import com.exchanger.exception.notfound.RateNotFoundException;
import com.exchanger.mapper.RateMapper;
import com.exchanger.model.Rate;
import com.exchanger.model.enums.Currency;
import com.exchanger.repository.RateRepository;
import com.exchanger.service.RateService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateServiceImpl implements RateService {

    private final RateMapper rateMapper;
    private final RateRepository rateRepository;

    @Value("${bank.rate.url}")
    private String bankUrl;

    @Override
    public List<Rate> getRates() {
        return rateRepository.findAll();
    }

    @Override
    public Rate getRateById(UUID id) {
        return rateRepository.findById(id)
                .orElseThrow(() -> new RateNotFoundException("Rate not found by id"));
    }

    @Override
    public Rate getRateByCurrency(Currency currency) {
        return rateRepository.findByCurrency(currency)
                .orElseThrow(() -> new RateNotFoundException("Rate not found by currency code"));
    }

    @Override
    public Rate getRateByCurrencyCode(String currencyCode) {
        Currency currency = Currency.getInstance(currencyCode);
        return getRateByCurrency(currency);
    }

    @Override
    @Scheduled(cron = "${schedule.cron.time.table}")
    /*
    +-------------------- second (0 - 59)
    |  +----------------- minute (0 - 59)
    |  |  +-------------- hour (0 - 23)
    |  |  |  +----------- day of month (1 - 31)
    |  |  |  |  +-------- month (1 - 12)
    |  |  |  |  |  +----- day of week (0 - 6) (Sunday=0 or 7)
    |  |  |  |  |  |  +-- year [optional]
    |  |  |  |  |  |  |
    *  *  *  *  *  *  * command to be executed
    */
    public void getRatesFromExternalSource() {
        log.info("Fetching exchange rates from the URL");
        JsonNode jNode = getJsonNodeFromExternalSource();
        jNode.forEach(this::processRate);
    }

    private JsonNode getJsonNodeFromExternalSource() {
        ResponseEntity<String> response = getResponseFromUrl(bankUrl);
        return getJsonNodeFromResponse(response);
    }

    private static ResponseEntity<String> getResponseFromUrl(String url) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForEntity(url, String.class);
    }

    private static JsonNode getJsonNodeFromResponse(ResponseEntity<String> response) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            String message = "Failed to parse json response";
            log.error(message, e);
            throw new ExternalHttpCallException(message);
        }
    }

    private void processRate(JsonNode node) {
        Rate newRate = rateMapper.fromJsonNodeToRate(node);
        Currency currency = newRate.getCurrency();

        Optional<Rate> existingRate = rateRepository.findByCurrency(currency);

        if (existingRate.isPresent()) {
            updateExistingRate(existingRate.get(), newRate);
        } else {
            addNewRate(newRate);
        }
    }

    private void updateExistingRate(Rate existingRate, Rate newRate) {
        log.info("Updating existing rate for currency: {}", existingRate.getCurrency());
        Rate updatedRate = rateMapper.updateRate(existingRate, newRate);
        rateRepository.save(updatedRate);
    }

    private void addNewRate(Rate newRate) {
        log.info("Adding new rate for currency: {}", newRate.getCurrency());
        rateRepository.save(newRate);
    }

}
