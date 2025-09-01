package ru.idles.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * @author a.zharov
 */
@Service
@Slf4j
public class KafkaConsumerService {
    @KafkaListener(topics = "${kafka.topics.topic1}", groupId = "pogrebok-group")
    public void listen(String message) {
        log.info("Получено сообщение: {}", message);
    }
}
