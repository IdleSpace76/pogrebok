package ru.idles.service;

/**
 * Интерфейс работы с активацией пользователя
 *
 * @author a.zharov
 */
public interface UserActivationService {
    boolean activateUser(String cryptoUserId);
}
