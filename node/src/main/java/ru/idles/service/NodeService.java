package ru.idles.service;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author a.zharov
 */
public interface NodeService {
    void processMsg(Update update);
}
