package ru.idles.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
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
        } catch (Exception e) {
            throw new RuntimeException("Failed to send message to Kafka", e);
        }
    }
}
