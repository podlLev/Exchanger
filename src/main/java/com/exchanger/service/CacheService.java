package com.exchanger.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CacheService {

    @CachePut(value = "otpCache", key = "#key")
    public String addValueToCache(String key, String value) {
        log.info("Adding value to cache: {} -> {}", key, value);
        return value;
    }

    @Cacheable(value = "otpCache", key = "#key")
    public String getValueFromCache(String key) {
        log.warn("Cache miss for key: {}", key);
        return "Value not found in cache!";
    }

}
