package ru.idles.service;

import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.idles.entity.BotDocument;
import ru.idles.entity.BotImage;
import ru.idles.enums.LinkType;

/**
 * @author a.zharov
 */
public interface FileService {
    BotDocument processDoc(Message externalMessage);
    BotImage processImage(Message externalMessage);
    String generateLink(Long fileId, LinkType linkType);
}
