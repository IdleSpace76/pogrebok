package ru.idles.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Проперти - наименования топиков
 *
 * @author a.zharov
 */
@Configuration
@ConfigurationProperties(prefix = "kafka.topics")
@Getter
@Setter
public class KafkaTopicsProperties {
    private String userMessages;
    private String nodeMessages;
    private String registrationMail;
}
