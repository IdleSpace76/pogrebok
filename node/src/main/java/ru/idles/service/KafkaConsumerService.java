package ru.idles.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.idles.entity.RawData;
import ru.idles.dao.RawDataRepository;

/**
 * @author a.zharov
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final NodeService nodeService;
    private final ObjectMapper objectMapper;
    private final RawDataRepository rawDataRepository;

    /**
     * Слушатель сообщений от пользователя
     */
    @KafkaListener(topics = "${kafka.topics.user-messages}")
    public void listenUserMsg(String message) throws JsonProcessingException {
        log.info("Получено сообщение от брокера: {}", message);
        Update updateFromBroker = objectMapper.readValue(message, Update.class);
        saveRawData(updateFromBroker);
        nodeService.processMsg(updateFromBroker);
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataRepository.save(rawData);
    }
}
