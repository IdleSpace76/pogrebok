package ru.idles.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.idles.config.KafkaTopicsProperties;

/**
 * Обработчик сообщений от пользователя
 *
 * @author a.zharov
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BotUpdateHandler {

    private final OkHttpTelegramClient telegramClient;
    private final KafkaProducerService kafkaProducerService;
    private final KafkaTopicsProperties kafkaTopicsProperties;

    public void handleUpdate(Update update) {
        Message msg = update.getMessage();
        log.info("Сообщение от пользователя [{}] : {}", msg.getFrom().getUserName(), msg.getText());

        if (update.hasMessage()) {
            processTextMessage(update);
        }
    }

    private void processTextMessage(Update update) {
        kafkaProducerService.sendObjectMessage(kafkaTopicsProperties.getUserMessages(), update);
    }

    public void sendAnswerMsg(SendMessage msg) {
        try {
            telegramClient.execute(msg);
        }
        catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения пользователю : {}", msg.getChatId(), e);
        }
    }
}
