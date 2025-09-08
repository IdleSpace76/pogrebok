package ru.idles.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author a.zharov
 */
@Configuration
@ConfigurationProperties(prefix = "telegram.bot")
@Getter
@Setter
public class TelegramProperties {
    private String token;
    private String fileInfoUri;
    private String fileStorageUri;
}
