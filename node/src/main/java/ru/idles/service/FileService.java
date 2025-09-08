package ru.idles.service;

import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.idles.entity.BotDocument;
import ru.idles.entity.BotImage;

/**
 * @author a.zharov
 */
public interface FileService {
    BotDocument processDoc(Message externalMessage);
    BotImage processImage(Message externalMessage);
}
