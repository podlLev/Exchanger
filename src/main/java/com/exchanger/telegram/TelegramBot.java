package com.exchanger.telegram;

import com.exchanger.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final UserService userService;

    @Value("${telegram.bot.username}")
    private String username;

    @Value("${telegram.bot.token}")
    private String token;

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = TelegramBotUtils.getChatId(update);

        if(update.hasMessage() && update.getMessage().getText().equals("/start")) {
            sendMessage(chatId,
                    "Please send your login email in international format", Map.of());
        } else {
            String message = userService.activateUser(update.getMessage().getText(), chatId);
            sendMessage(chatId, message, Map.of());
        }
    }

    public void sendMessage(Long chatId, String text) {
        sendApiMethodAsync(TelegramBotUtils.createMessage(chatId, text));
    }

    public void sendMessage(Long chatId, String text, Map<String, String> buttons) {
        sendApiMethodAsync(TelegramBotUtils.createMessage(chatId, text, buttons));
    }

}
