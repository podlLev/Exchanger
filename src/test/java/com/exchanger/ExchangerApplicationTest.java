package com.exchanger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.TaskScheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExchangerApplicationTest {

    @Autowired
    private CacheManager cacheManager;

    @Autowired(required = false)
    private TaskScheduler taskScheduler;

    @Test
    void cacheManagerShouldBeInitialized() {
        assertThat(cacheManager).isNotNull();
    }

    @Test
    void taskSchedulerShouldBeInitialized() {
        assertThat(taskScheduler).isNotNull();
    }

    @Test
    void mainMethodShouldRunWithoutExceptions() {
        assertDoesNotThrow(() -> ExchangerApplication.main(new String[]{}));
    }

}