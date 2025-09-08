package ru.idles.exception;

/**
 * @author a.zharov
 */
public class UploadFileException extends RuntimeException {
    public UploadFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public UploadFileException(String message) {
        super(message);
    }

    public UploadFileException(Throwable cause) {
        super(cause);
    }
}
