package ru.idles.exception;

/**
 * @author a.zharov
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
