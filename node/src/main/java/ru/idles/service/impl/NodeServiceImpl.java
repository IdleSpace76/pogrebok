package ru.idles.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.idles.config.KafkaTopicsProperties;
import ru.idles.dto.NodeMsgDto;
import ru.idles.entity.BotDocument;
import ru.idles.entity.BotImage;
import ru.idles.entity.BotUser;
import ru.idles.enums.BotCommands;
import ru.idles.enums.UserState;
import ru.idles.dao.BotUserRepository;
import ru.idles.exception.UploadFileException;
import ru.idles.service.FileService;
import ru.idles.service.KafkaProducerService;
import ru.idles.service.NodeService;

/**
 * @author a.zharov
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NodeServiceImpl implements NodeService {

    private final KafkaProducerService kafkaProducerService;
    private final KafkaTopicsProperties kafkaTopicsProperties;
    private final BotUserRepository botUserRepository;
    private final FileService fileService;

    private static final String UNKNOWN_ERROR_TEXT = "Неизвестная ошибка, введите /cancel и попробуйте снова";

    public void processMsg(Update update) {
        Message message = update.getMessage();
        if (message.hasText()) {
            processTextMessage(message);
        }
        else if (message.hasDocument()) {
            processDocMessage(message);
        }
        else if (message.hasPhoto()) {
            processImageMessage(message);
        }
        else {
            processUnsupported(message);
        }
    }

    private void processTextMessage(Message userMsg) {
        String userText = userMsg.getText();

        BotUser botUser = findOrSaveBotUser(userMsg.getFrom());
        UserState userState = botUser.getState();

        String outputText = null;

        if (BotCommands.CANCEL.isEqual(userText)) {
            outputText = cancelProcess(botUser);
        }
        else if (UserState.BASIC_STATE.equals(userState)) {
            outputText = processServiceCommand(userText);
        }
        else if (UserState.WAIT_FOR_EMAIL_STATE.equals(userState)) {
            // TODO обработка
        }
        else {
            log.error("Неизвестное состояние пользователя: {}", userState);
            outputText = UNKNOWN_ERROR_TEXT;
        }

        String chatId = userMsg.getChatId().toString();
        sendAnswer(outputText, chatId);
    }

    private void processDocMessage(Message userMsg) {
        BotUser botUser = findOrSaveBotUser(userMsg.getFrom());
        String chatId = userMsg.getChatId().toString();
        if (isNotAllowedToSendContent(chatId, botUser)) {
            return;
        }
        try {
            BotDocument doc = fileService.processDoc(userMsg);
            // TODO генерация ссылки для скачивания
            String answerText = "Документ успешно загружен! Ссылка для скачивания: http://test.ru/doc/777";
            sendAnswer(answerText, chatId);
        }
        catch (UploadFileException e) {
            log.error("Загрузка не удалась", e);
            String errorText = "Загрузка документа не удалась. Повторите попытку позже";
            sendAnswer(errorText, chatId);
        }
    }

    private void processImageMessage(Message userMsg) {
        BotUser botUser = findOrSaveBotUser(userMsg.getFrom());
        String chatId = userMsg.getChatId().toString();
        if (isNotAllowedToSendContent(chatId, botUser)) {
            return;
        }

        try {
            BotImage botImage = fileService.processImage(userMsg);
            // TODO Генерация ссылки
            String answerText = "Изображение успешно загружено! Ссылка для скачивания: http://test.ru/photo/777";
            sendAnswer(answerText, chatId);
        }
        catch (UploadFileException e) {
            log.error("Загрузка не удалась", e);
            String errorText = "Загрузка изображения не удалась. Повторите попытку позже";
            sendAnswer(errorText, chatId);
        }
    }

    private void processUnsupported(Message userMsg) {
        // TODO реализация
    }

    private String processServiceCommand(String userText) {
        BotCommands enumCommand = BotCommands.findByCmd(userText);
        if (enumCommand != null) {
            switch (enumCommand) {
                case HELP -> {
                    return helpProcess();
                }
                case REGISTRATION -> {
                    return registrationProcess();
                }
                case START -> {
                    return startProcess();
                }
                default -> {
                    return UNKNOWN_ERROR_TEXT;
                }
            }
        }
        else {
            return "Неизвестная команда, введите /help";
        }
    }

    private String cancelProcess(BotUser botUser) {
        botUser.setState(UserState.BASIC_STATE);
        botUserRepository.save(botUser);
        return "Команда отменена!";
    }

    private String startProcess() {
        return "Приветствую! Чтобы посмотреть список доступных команд, введи /help";
    }

    private String registrationProcess() {
        return "Временно недоступен";
    }

    private String helpProcess() {
        return BotCommands.commandHelpNote();
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

    private boolean isAllowedToSendContent(String chatId, BotUser botUser) {
        UserState userState = botUser.getState();
        if (!botUser.getIsActive()) {
            String errorText = "Зарегистрируйтесь или активируйте свою учетную запись для загрузки контента";
            sendAnswer(errorText, chatId);
            return false;
        }
        else if (!UserState.BASIC_STATE.equals(userState)) {
            String errorText = "Отмените текущую команду с помощью команды /cancel";
            sendAnswer(errorText, chatId);
            return false;
        }
        return true;
    }

    private boolean isNotAllowedToSendContent(String chatId, BotUser botUser) {
        return !isAllowedToSendContent(chatId, botUser);
    }

    private void sendAnswer(String outputText, String chatId) {
        NodeMsgDto nodeMsgObject = NodeMsgDto.builder()
                .text(outputText)
                .chatId(chatId)
                .build();
        kafkaProducerService.sendObjectMessage(kafkaTopicsProperties.getNodeMessages(), nodeMsgObject);
    }
}
