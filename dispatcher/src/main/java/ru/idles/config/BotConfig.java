package ru.idles.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.idles.controller.TelegramBot;

/**
 * @author a.zharov
 */
@Configuration
@RequiredArgsConstructor
public class BotConfig {

    private final BotProperties botProperties;

    /**
     * Регистрация бота
     */
    @Bean
    public TelegramBotsLongPollingApplication botRegistration(TelegramBot telegramBot) throws TelegramApiException {
        TelegramBotsLongPollingApplication telegramBotsLongPollingApplication = new TelegramBotsLongPollingApplication();
        telegramBotsLongPollingApplication.registerBot(botProperties.getToken(), telegramBot);
        return telegramBotsLongPollingApplication;
    }

    /**
     * ТГ клиент (для отправки сообщений)
     */
    @Bean
    public OkHttpTelegramClient telegramClient() {
        return new OkHttpTelegramClient(botProperties.getToken());
    }
}
