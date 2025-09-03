package ru.idles.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author a.zharov
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final NodeService  nodeService;

    @KafkaListener(topics = "${kafka.topics.user-messages}")
    public void listen(String message) throws JsonProcessingException {
        log.info("Получено сообщение из Kafka: {}", message);
        nodeService.processTextMessage(message);
    }
}
