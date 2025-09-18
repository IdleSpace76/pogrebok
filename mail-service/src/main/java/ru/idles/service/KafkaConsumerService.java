package ru.idles.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.idles.dto.MailParams;

/**
 * @author a.zharov
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;
    private final MailService mailService;

    /**
     * Слушатель сообщений о регистрации почты
     */
    @KafkaListener(topics = "${kafka.topics.registration-mail}")
    public void listenRegistrationMail(String message) throws JsonProcessingException {
        log.info("Получено сообщение от брокера: {}", message);
        MailParams mailParams = objectMapper.readValue(message, MailParams.class);
        mailService.sendMail(mailParams);
    }
}
