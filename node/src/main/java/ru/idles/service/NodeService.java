package ru.idles.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.idles.config.KafkaTopicsProperties;
import ru.idles.dto.NodeMsgDto;
import ru.idles.entity.BotUser;
import ru.idles.entity.RawData;
import ru.idles.enums.UserState;
import ru.idles.repository.BotUserRepository;
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
    private final BotUserRepository botUserRepository;

    private static final String NODE_HELLO_ANSWER = "Hello from NODE!";

    public void processTextMessage(String message) throws JsonProcessingException {
        Update update = objectMapper.readValue(message, Update.class);
        saveRawData(update);
        Message msg = update.getMessage();
        BotUser botUser = findOrSaveBotUser(msg.getFrom());

        String chatId = msg.getChatId().toString();
        NodeMsgDto nodeMsgObject = NodeMsgDto.builder()
                .text(NODE_HELLO_ANSWER)
                .chatId(chatId)
                .build();
        kafkaProducerService.sendObjectMessage(kafkaTopicsProperties.getNodeMessages(), nodeMsgObject);
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataRepository.save(rawData);
    }

    private BotUser findOrSaveBotUser(User telegramUser) {
        BotUser botUser = botUserRepository.findBotUserByTelegramUserId(telegramUser.getId());
        if (botUser == null) {
            BotUser transientBotUser = BotUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    // TODO изменить значение по умолчанию
                    .isActive(true)
                    .state(UserState.BASIC_STATE)
                    .build();
            return botUserRepository.save(transientBotUser);
        }
        return botUser;
    }
}
