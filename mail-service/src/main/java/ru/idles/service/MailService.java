package ru.idles.service;

import ru.idles.dto.MailParams;

/**
 * Интерфейс работы с отправкой электронных сообщений
 *
 * @author a.zharov
 */
public interface MailService {
    void sendMail(MailParams mailParams);
}
