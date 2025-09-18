package ru.idles.service.impl;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import ru.idles.config.KafkaTopicsProperties;
import ru.idles.dao.BotUserRepository;
import ru.idles.dto.MailParams;
import ru.idles.entity.BotUser;
import ru.idles.enums.UserState;
import ru.idles.service.BotUserService;
import ru.idles.service.KafkaProducerService;
import ru.idles.utils.CryptoTool;

import java.util.Optional;

/**
 * @author a.zharov
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BotUserServiceImpl implements BotUserService {

    private final BotUserRepository botUserRepository;
    private final CryptoTool cryptoTool;
    private final WebClient webClient;
    private final KafkaProducerService kafkaProducerService;
    private final KafkaTopicsProperties kafkaTopicsProperties;

    @Override
    public String registerUser(BotUser user) {
        if (user.getIsActive()) {
            return "Вы уже зарегистрированы!";
        }
        else if (user.getEmail() != null) {
            return "На почту уже было отправлено письмо регистрации. Перейдите по ссылке в письме для завершения.";
        }

        user.setState(UserState.WAIT_FOR_EMAIL_STATE);
        botUserRepository.save(user);
        return "Введите ваш email: ";
    }

    @Override
    @Transactional
    public String setEmail(BotUser user, String inputEmail) {
        String email = normalizeEmail(inputEmail);
        try {
            new InternetAddress(email).validate();
        }
        catch (AddressException e) {
            return "Введите корректный email. Для отмены команды введите /cancel";
        }
        Optional<BotUser> botUser = botUserRepository.findByEmail(email);
        if (botUser.isEmpty()) {
            user.setEmail(email);
            user.setState(UserState.BASIC_STATE);
            user = botUserRepository.save(user);

            String cryptoUserId = cryptoTool.hashOf(user.getId());
            sendRegistrationMsg(cryptoUserId, email);
            return "На почту было отправлено письмо с ссылкой для подтверждения регистрации";
        }
        else {
            return "Этот email уже используется. Введите корректный email. Для отмены команды введите /cancel";
        }
    }

    private void sendRegistrationMsg(String cryptoUserId, String email) {
        MailParams mailParams = MailParams.builder()
                .id(cryptoUserId)
                .mailTo(email)
                .build();

        kafkaProducerService.sendObjectMessageAfterCommit(kafkaTopicsProperties.getRegistrationMail(), mailParams);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
