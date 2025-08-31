package ru.idles.Controller;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.BotOptions;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import ru.idles.config.BotProperties;
import ru.idles.service.BotUpdateHandler;

/**
 * @author a.zharov
 */
@Component
public class TelegramBot implements LongPollingBot {

    private final BotUpdateHandler updateHandler;
    private final BotProperties botProperties;
    private final DefaultBotOptions botOptions;

    public TelegramBot(BotUpdateHandler updateHandler, BotProperties botProperties) {
        this.updateHandler = updateHandler;
        this.botProperties = botProperties;
        this.botOptions = new DefaultBotOptions();
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateHandler.handleUpdate(update);
    }

    @Override
    public BotOptions getOptions() {
        return this.botOptions;
    }

    @Override
    public void clearWebhook() throws TelegramApiRequestException {

    }

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }
}
