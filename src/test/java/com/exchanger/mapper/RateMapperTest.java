package com.exchanger.mapper;

import com.exchanger.model.Rate;
import com.exchanger.model.enums.Currency;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class RateMapperTest {

    private RateMapper rateMapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        rateMapper = Mappers.getMapper(RateMapper.class);
        objectMapper = new ObjectMapper();
    }

    @Test
    void fromJsonNodeToRate() throws JsonProcessingException {
        String json = "{\"ccy\": \"USD\", \"buy\": \"1.2\", \"sale\": \"1.3\"}";
        JsonNode jsonNode = objectMapper.readTree(json);

        Rate rate = rateMapper.fromJsonNodeToRate(jsonNode);

        assertNotNull(rate);
        assertEquals(Currency.USD, rate.getCurrency());
        assertEquals(new BigDecimal("1.2"), rate.getBuy());
        assertEquals(new BigDecimal("1.3"), rate.getSale());
        assertNotNull(rate.getReceive());
    }

    @Test
    void fromJsonNodeToRate_empty() {
        Rate rate = rateMapper.fromJsonNodeToRate(null);
        assertNull(rate);
    }

    @Test
    void updateRate() {
        Rate target = new Rate()
                .setCurrency(Currency.EUR)
                .setBuy(new BigDecimal("1.1"))
                .setSale(new BigDecimal("1.2"))
                .setReceive(new Timestamp(System.currentTimeMillis()));

        Rate source = new Rate()
                .setBuy(new BigDecimal("1.15"))
                .setSale(new BigDecimal("1.25"));

        rateMapper.updateRate(target, source);

        assertEquals(source.getBuy(), target.getBuy());
        assertEquals(source.getSale(), target.getSale());
    }

    @Test
    void updateRate_empty() {
        Rate target = new Rate();

        rateMapper.updateRate(target, null);

        assertNotNull(target);
    }

}
