package ru.idles.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author a.zharov
 */
@Configuration
@ConfigurationProperties(prefix = "crypto")
@Getter
@Setter
public class CryptoProperties {
    private String salt;
}
