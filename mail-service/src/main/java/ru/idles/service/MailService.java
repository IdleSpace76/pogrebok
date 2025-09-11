package ru.idles.service;

import ru.idles.dto.MailParams;

/**
 * @author a.zharov
 */
public interface MailService {
    void sendMail(MailParams mailParams);
}
