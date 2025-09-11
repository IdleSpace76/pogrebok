package ru.idles.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.idles.utils.CryptoTool;

/**
 * @author a.zharov
 */
@Configuration
@RequiredArgsConstructor
public class FileServiceConfig {

     private final CryptoProperties cryptoProperties;

    @Bean
    public CryptoTool cryptoTool() {
        return new CryptoTool(cryptoProperties.getSalt());
    }
}
