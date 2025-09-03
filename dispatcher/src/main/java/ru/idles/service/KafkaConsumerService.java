package ru.idles.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.idles.dto.NodeMsgDto;

/**
 * @author a.zharov
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final BotUpdateHandler botUpdateHandler;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topics.node-messages}")
    public void listen(String message) throws JsonProcessingException {
        log.info("Получено сообщение из Kafka: {}", message);
        NodeMsgDto dto = objectMapper.readValue(message, NodeMsgDto.class);
        SendMessage answerMsg = SendMessage.builder()
                .chatId(dto.getChatId())
                .text(dto.getText())
                .build();
        botUpdateHandler.sendAnswerMsg(answerMsg);
    }
}
