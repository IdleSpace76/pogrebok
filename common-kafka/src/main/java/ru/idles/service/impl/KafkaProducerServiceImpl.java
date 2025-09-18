package ru.idles.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ru.idles.service.KafkaProducerService;

/**
 * @author a.zharov
 */
@Service
@RequiredArgsConstructor
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void sendTextMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

    @Override
    public void sendObjectMessage(String topic, Object object) {
        try {
            String json = objectMapper.writeValueAsString(object);
            kafkaTemplate.send(topic, json);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to send message to Kafka", e);
        }
    }

    @Override
    public void sendObjectMessageAfterCommit(String topic, Object object) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    sendObjectMessage(topic, object);
                }
            });
        }
        else {
            sendObjectMessage(topic, object);
        }
    }
}
