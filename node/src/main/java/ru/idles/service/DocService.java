package ru.idles.service;

import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.idles.entity.BotDocument;

/**
 * @author a.zharov
 */
public interface DocService {
    BotDocument processDoc(Message externalMessage);
}
