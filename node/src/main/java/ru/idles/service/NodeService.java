package ru.idles.service;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Обработка сообщения
 *
 * @author a.zharov
 */
public interface NodeService {
    void processMsg(Update update);
}
