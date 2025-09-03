package ru.idles.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.idles.config.KafkaTopicsProperties;
import ru.idles.dto.NodeMsgDto;

/**
 * @author a.zharov
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final KafkaProducerService kafkaProducerService;
    private final KafkaTopicsProperties kafkaTopicsProperties;
    private final ObjectMapper objectMapper;

    private static final String NODE_HELLO_ANSWER = "Hello from NODE!";

    @KafkaListener(topics = "${kafka.topics.user-messages}")
    public void listen(String message) throws JsonProcessingException {
        log.info("Получено сообщение из Kafka: {}", message);
        JsonNode json = objectMapper.readTree(message);
        String chatId = json.path("chat").path("id").asText();
        NodeMsgDto nodeMsgObject = NodeMsgDto.builder().text(NODE_HELLO_ANSWER).chatId(chatId).build();
        kafkaProducerService.sendObjectMessage(kafkaTopicsProperties.getNodeMessages(), nodeMsgObject);
    }
}
