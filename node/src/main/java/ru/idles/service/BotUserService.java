package ru.idles.service;

import ru.idles.entity.BotUser;

/**
 * @author a.zharov
 */
public interface BotUserService {
    String registerUser(BotUser user);
    String setEmail(BotUser user, String email);
}
