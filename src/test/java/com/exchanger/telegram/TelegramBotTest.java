package com.exchanger.telegram;

import com.exchanger.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

import static org.mockito.Mockito.*;

class TelegramBotTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private TelegramBot telegramBot;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        telegramBot = new TelegramBot(userService);
        telegramBot = spy(telegramBot);
        doReturn("test-bot-token").when(telegramBot).getBotToken();
        doReturn("test-bot-username").when(telegramBot).getBotUsername();
    }

    @Test
    void testOnUpdateReceived_sendsPromptMessage() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.getText()).thenReturn("/start");

        try (MockedStatic<TelegramBotUtils> mocked = mockStatic(TelegramBotUtils.class)) {
            mocked.when(() -> TelegramBotUtils.getChatId(update)).thenReturn(12345L);

            doNothing().when(telegramBot).sendMessage(eq(12345L), anyString(), anyMap());

            telegramBot.onUpdateReceived(update);

            verify(telegramBot).sendMessage(12345L,
                    "Please send your login email in international format", Map.of());
        }
    }

    @Test
    void testOnUpdateReceived_activatesUserAndSendsResponse() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.getText()).thenReturn("user@example.com");

        try (MockedStatic<TelegramBotUtils> mocked = mockStatic(TelegramBotUtils.class)) {
            mocked.when(() -> TelegramBotUtils.getChatId(update)).thenReturn(54321L);

            when(userService.activateUser("user@example.com", 54321L))
                    .thenReturn("Activation successful!");

            doNothing().when(telegramBot).sendMessage(eq(54321L), anyString(), anyMap());

            telegramBot.onUpdateReceived(update);

            verify(userService).activateUser("user@example.com", 54321L);
            verify(telegramBot).sendMessage(54321L, "Activation successful!", Map.of());
        }
    }

    @Test
    void testSendMessageWithTextOnly() {
        Long chatId = 123456L;
        String text = "Hello";

        SendMessage expectedMessage = TelegramBotUtils.createMessage(chatId, text);

        try (var mocked = mockStatic(TelegramBotUtils.class)) {
            mocked.when(() -> TelegramBotUtils.createMessage(chatId, text)).thenReturn(expectedMessage);

            telegramBot.sendMessage(chatId, text);
        }
    }

    @Test
    void testSendMessageWithButtons() {
        Long chatId = 123456L;
        String text = "Choose";
        Map<String, String> buttons = Map.of("Yes", "yes_callback", "No", "no_callback");

        SendMessage expectedMessage = TelegramBotUtils.createMessage(chatId, text, buttons);

        try (var mocked = mockStatic(TelegramBotUtils.class)) {
            mocked.when(() -> TelegramBotUtils.createMessage(chatId, text, buttons)).thenReturn(expectedMessage);

            telegramBot.sendMessage(chatId, text, buttons);
        }
    }

}