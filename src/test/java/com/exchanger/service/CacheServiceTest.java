package com.exchanger.service;

import com.exchanger.config.RedisCacheConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@EnableCaching
@Import(RedisCacheConfig.class)
class CacheServiceTest {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void testCachePutAndGet() {
        String key = "testKey";
        String value = "testValue";

        cacheService.addValueToCache(key, value);
        Cache cache = cacheManager.getCache("otpCache");

        assertNotNull(cache);
        assertEquals(value, cache.get(key, String.class));
        assertEquals(value, cacheService.getValueFromCache(key));
    }

    @Test
    void testCacheMiss() {
        String key = "nonExistentKey";
        String value = cacheService.getValueFromCache(key);

        assertEquals("Value not found in cache!", value);
    }

    @Test
    void testCacheUpdate() {
        String key = "updateKey";

        cacheService.addValueToCache(key, "initialValue");
        assertEquals("initialValue", cacheService.getValueFromCache(key));

        cacheService.addValueToCache(key, "updatedValue");
        assertEquals("updatedValue", cacheService.getValueFromCache(key));
    }

}
