package ru.idles.service.impl;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import ru.idles.dao.BotUserRepository;
import ru.idles.dto.MailParams;
import ru.idles.entity.BotUser;
import ru.idles.enums.UserState;
import ru.idles.service.BotUserService;
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

    @Value("${service.mail.uri}")
    private String mailServiceUri;

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
            user =  botUserRepository.save(user);

            String cryptoUserId = cryptoTool.hashOf(user.getId());
            ResponseEntity<String> response = sendRequestToMailService(cryptoUserId, email);
            if (response.getStatusCode() != HttpStatus.OK) {
                String msg = String.format("Не удалось отправить письмо на адрес : %s", email);
                log.error(msg);
                user.setEmail(null);
                botUserRepository.save(user);
                return msg;
            }
            return "На почту было отправлено письмо с ссылкой для подтверждения регистрации";
        }
        else {
            return "Этот email уже используется. Введите корректный email. Для отмены команды введите /cancel";
        }
    }

    private ResponseEntity<String> sendRequestToMailService(String cryptoUserId, String email) {
        MailParams mailParams = MailParams.builder()
                .id(cryptoUserId)
                .mailTo(email)
                .build();

        try {
            return webClient.post()
                    .uri(mailServiceUri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(mailParams)
                    .exchangeToMono(resp -> resp.toEntity(String.class))
                    .onErrorResume(WebClientResponseException.class, e ->
                            Mono.just(ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString())))
                    .onErrorResume(Throwable.class, e ->
                            Mono.just(ResponseEntity.status(500).body("Ошибка вызова mail сервиса: " + e.getMessage())))
                    .block();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка вызова mail сервиса: " + e.getMessage());
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
