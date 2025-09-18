package ru.idles.exception;

/**
 * Исключение - Не найдено содержимое документа
 *
 * @author a.zharov
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
