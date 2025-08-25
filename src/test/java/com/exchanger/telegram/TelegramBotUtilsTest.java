package com.exchanger.telegram;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TelegramBotUtilsTest {

    @Test
    void testGetChatId_withMessage() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        User user = mock(User.class);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(123L);

        Long result = TelegramBotUtils.getChatId(update);
        assertEquals(123L, result);
    }

    @Test
    void testGetChatId_withCallbackQuery() {
        Update update = mock(Update.class);
        CallbackQuery callbackQuery = mock(CallbackQuery.class);
        User user = mock(User.class);

        when(update.hasMessage()).thenReturn(false);
        when(update.hasCallbackQuery()).thenReturn(true);
        when(update.getCallbackQuery()).thenReturn(callbackQuery);
        when(callbackQuery.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(456L);

        Long result = TelegramBotUtils.getChatId(update);
        assertEquals(456L, result);
    }

    @Test
    void testGetChatId_withNeitherMessageNorCallbackQuery() {
        Update update = mock(Update.class);
        when(update.hasMessage()).thenReturn(false);
        when(update.hasCallbackQuery()).thenReturn(false);

        Long result = TelegramBotUtils.getChatId(update);
        assertNull(result);
    }

    @Test
    void testCreateMessage_basic() {
        SendMessage message = TelegramBotUtils.createMessage(123L, "Hello");
        assertEquals("Hello", message.getText());
        assertEquals("markdown", message.getParseMode());
        assertEquals("123", message.getChatId());
        assertNull(message.getReplyMarkup());
    }

    @Test
    void testCreateMessage_withButtons() {
        Map<String, String> buttons = Map.of("Yes", "yes_callback", "No", "no_callback");

        SendMessage message = TelegramBotUtils.createMessage(123L, "Confirm?", buttons);

        assertEquals("Confirm?", message.getText());
        assertEquals("markdown", message.getParseMode());
        assertEquals("123", message.getChatId());
        assertNotNull(message.getReplyMarkup());
    }

    @Test
    void testCreateMessage_withEmptyButtons() {
        SendMessage message = TelegramBotUtils.createMessage(123L, "Test", Map.of());
        assertNull(message.getReplyMarkup());
    }

    @Test
    void testCreateMessage_withNullButtons() {
        SendMessage message = TelegramBotUtils.createMessage(123L, "Test", null);
        assertNull(message.getReplyMarkup());
    }

}
