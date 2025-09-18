package ru.idles.service;

/**
 * Интерфейс по отправке сообщений Кафка
 *
 * @author a.zharov
 */
public interface KafkaProducerService {
    ///  Отправить текстовое сообщение
    void sendTextMessage(String topic, String message);
    ///  Отправить сущность
    void sendObjectMessage(String topic, Object object);
    /// Отправить сущность в транзакции
    void sendObjectMessageAfterCommit(String topic, Object object);
}
