package ru.idles.service;

/**
 * @author a.zharov
 */
public interface KafkaProducerService {
    void sendTextMessage(String topic, String message);
    void sendObjectMessage(String topic, Object object);
}
