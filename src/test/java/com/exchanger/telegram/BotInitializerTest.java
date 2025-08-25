package com.exchanger.telegram;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.mockito.Mockito.*;

class BotInitializerTest {

    @Mock
    private TelegramBot bot;

    @Mock
    private TelegramBotsApi telegramBotsApi;

    @InjectMocks
    private BotInitializer botInitializer;

    private LogCaptor logCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        logCaptor = LogCaptor.forClass(BotInitializer.class);
    }

    @Test
    void init() throws TelegramApiException {
        botInitializer.init();
        verify(telegramBotsApi, times(1)).registerBot(bot);
        assert logCaptor.getErrorLogs().isEmpty();
    }

    @Test
    void init_LogErrorWhenTelegramApiExceptionThrown() throws TelegramApiException {
        doThrow(new TelegramApiException("Registration failed")).when(telegramBotsApi).registerBot(bot);
        botInitializer.init();
        verify(telegramBotsApi).registerBot(bot);
        assert logCaptor.getErrorLogs().stream()
                .anyMatch(msg -> msg.contains("ailed to register the bot with Telegram API"));
    }

}