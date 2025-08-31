package ru.idles.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author a.zharov
 */
@Service
public class BotUpdateHandler {

    public void handleUpdate(Update update) {
        Message message = update.getMessage();
        System.out.println(message.getText());
    }
}
