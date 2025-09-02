package ru.idles.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * @author a.zharov
 */
@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {

    private final KafkaTopicsProperties kafkaTopicsProperties;

    @Bean
    public NewTopic botMessagesTopic() {
        return TopicBuilder.name(kafkaTopicsProperties.getBotMessages())
                .partitions(3)
                .replicas(1)
                .build();
    }
}
