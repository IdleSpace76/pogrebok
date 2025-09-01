package ru.idles.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.idles.service.BotUpdateHandler;

/**
 * @author a.zharov
 */
@Component
@RequiredArgsConstructor
public class TelegramBot implements LongPollingSingleThreadUpdateConsumer {

    private final BotUpdateHandler botUpdateHandler;

    @Override
    public void consume(Update update) {
        botUpdateHandler.handleUpdate(update);
    }
}
