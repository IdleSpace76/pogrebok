package ru.idles.service;

import ru.idles.entity.BotUser;

/**
 * Интерфейс работы с данными пользователя
 *
 * @author a.zharov
 */
public interface BotUserService {
    String registerUser(BotUser user);
    String setEmail(BotUser user, String email);
}
