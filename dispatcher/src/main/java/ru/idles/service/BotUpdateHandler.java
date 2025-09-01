package ru.idles.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @author a.zharov
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BotUpdateHandler {

    private final OkHttpTelegramClient telegramClient;

    private static final String HELLO_BOT_ANSWER = "Hello from bot!";

    public void handleUpdate(Update update) {
        Message msg = update.getMessage();
        log.info("That's what she said : [{}] : {}", msg.getFrom().getUserName(), msg.getText());

        if (msg.hasText()) {
            sendAnswerMsg(msg);
        }
    }

    public void sendAnswerMsg(Message msg) {
        SendMessage answerMsg = SendMessage.builder()
                .chatId(msg.getChatId().toString())
                .text(HELLO_BOT_ANSWER)
                .build();

        try {
            telegramClient.execute(answerMsg);
        }
        catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения пользователю : {}", msg.getChatId(), e);
        }
    }
}
