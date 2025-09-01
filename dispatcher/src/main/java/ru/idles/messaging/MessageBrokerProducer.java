package ru.idles.messaging;

/**
 * @author a.zharov
 */
public interface MessageBrokerProducer {
    void send(String topic, String message);
}
