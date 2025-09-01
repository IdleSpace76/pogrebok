package ru.idles.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * @author a.zharov
 */
@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic botMessagesTopic() {
        return TopicBuilder.name("bot-messages")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
