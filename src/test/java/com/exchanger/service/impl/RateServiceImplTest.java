package com.exchanger.service.impl;

import com.exchanger.exception.notfound.CurrencyNotFoundException;
import com.exchanger.exception.notfound.RateNotFoundException;
import com.exchanger.mapper.RateMapper;
import com.exchanger.model.Rate;
import com.exchanger.model.enums.Currency;
import com.exchanger.repository.RateRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateServiceImplTest {

    @Mock
    private RateRepository rateRepository;

    @Mock
    private RateMapper rateMapper;

    @InjectMocks
    private RateServiceImpl rateService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(rateService, "bankUrl", "https://mock-bank-url.com");
    }

    @Test
    void testGetRates() {
        List<Rate> mockRates = List.of(new Rate(), new Rate());
        when(rateRepository.findAll()).thenReturn(mockRates);

        List<Rate> result = rateService.getRates();

        assertEquals(2, result.size());
        verify(rateRepository).findAll();
    }

    @Test
    void testGetRateById() {
        UUID id = UUID.randomUUID();
        Rate rate = new Rate();
        when(rateRepository.findById(id)).thenReturn(Optional.of(rate));

        assertEquals(rate, rateService.getRateById(id));
    }

    @Test
    void testGetRateById_notFound() {
        UUID id = UUID.randomUUID();
        when(rateRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RateNotFoundException.class, () -> rateService.getRateById(id));
    }

    @Test
    void testGetRateByCurrency() {
        Currency currency = Currency.getInstance("USD");
        Rate rate = new Rate();
        when(rateRepository.findByCurrency(currency)).thenReturn(Optional.of(rate));

        assertEquals(rate, rateService.getRateByCurrency(currency));
    }

    @Test
    void testGetRateByCurrency_notFound() {
        Currency currency = Currency.getInstance("USD");
        when(rateRepository.findByCurrency(currency)).thenReturn(Optional.empty());

        assertThrows(RateNotFoundException.class, () -> rateService.getRateByCurrency(currency));
    }

    @Test
    void testGetRateByCurrencyCode() {
        Currency currency = Currency.getInstance("USD");
        Rate rate = new Rate();
        when(rateRepository.findByCurrency(currency)).thenReturn(Optional.of(rate));

        assertEquals(rate, rateService.getRateByCurrencyCode("USD"));
    }

    @Test
    void testGetRateByCurrencyCode_invalidCode() {
        assertThrows(CurrencyNotFoundException.class, () ->
                rateService.getRateByCurrencyCode("INVALID_CODE"));
    }

    @Test
    void testProcessRate_WhenRateExists_ShouldUpdate() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        JsonNode mockNode = mock(JsonNode.class);

        Currency currency = Currency.getInstance("USD");
        Rate newRate = new Rate(); newRate.setCurrency(currency);
        Rate existingRate = new Rate(); existingRate.setCurrency(currency);
        Rate updatedRate = new Rate();

        when(rateMapper.fromJsonNodeToRate(mockNode)).thenReturn(newRate);
        when(rateRepository.findByCurrency(currency)).thenReturn(Optional.of(existingRate));
        when(rateMapper.updateRate(existingRate, newRate)).thenReturn(updatedRate);

        Method method = RateServiceImpl.class.getDeclaredMethod("processRate", JsonNode.class);
        method.setAccessible(true);
        method.invoke(rateService, mockNode);

        verify(rateRepository).save(updatedRate);
    }

    @Test
    void testProcessRate_WhenRateDoesNotExist_ShouldAddNew() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        JsonNode mockNode = mock(JsonNode.class);

        Currency currency = Currency.getInstance("USD");
        Rate newRate = new Rate(); newRate.setCurrency(currency);

        when(rateMapper.fromJsonNodeToRate(mockNode)).thenReturn(newRate);
        when(rateRepository.findByCurrency(currency)).thenReturn(Optional.empty());

        Method method = RateServiceImpl.class.getDeclaredMethod("processRate", JsonNode.class);
        method.setAccessible(true);
        method.invoke(rateService, mockNode);

        verify(rateRepository).save(newRate);
    }

    @Test
    void testGetJsonNodeFromResponse_InvalidJson_ShouldThrow() {
        ResponseEntity<String> response = new ResponseEntity<>("INVALID", HttpStatus.OK);

        assertThrows(InvocationTargetException.class, () -> {
            Method method = RateServiceImpl.class.getDeclaredMethod("getJsonNodeFromResponse", ResponseEntity.class);
            method.setAccessible(true);
            method.invoke(rateService, response);
        });
    }

}
