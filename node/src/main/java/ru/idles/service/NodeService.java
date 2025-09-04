package ru.idles.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.idles.config.KafkaTopicsProperties;
import ru.idles.dto.NodeMsgDto;
import ru.idles.entity.RawData;
import ru.idles.repository.RawDataRepository;

/**
 * @author a.zharov
 */
@Service
@RequiredArgsConstructor
public class NodeService {

    private final RawDataRepository rawDataRepository;
    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper;
    private final KafkaTopicsProperties kafkaTopicsProperties;

    private static final String NODE_HELLO_ANSWER = "Hello from NODE!";

    public void processTextMessage(String message) throws JsonProcessingException {
        Update update = objectMapper.readValue(message, Update.class);
        saveRawData(update);

        String chatId = update.getMessage().getChatId().toString();
        NodeMsgDto nodeMsgObject = NodeMsgDto.builder().text(NODE_HELLO_ANSWER).chatId(chatId).build();
        kafkaProducerService.sendObjectMessage(kafkaTopicsProperties.getNodeMessages(), nodeMsgObject);
    }

    private void saveRawData(Update update) {
        RawData rawData = new RawData();
        rawData.setEvent(update);
        rawDataRepository.save(rawData);
    }

}
