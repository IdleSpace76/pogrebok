package ru.idles.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;
import ru.idles.dto.MailParams;

class KafkaProducerServiceImplTest {

    @Test
    void sendObjectMessage_serializesAndSends() throws Exception {
        KafkaTemplate<String, String> template = Mockito.mock(KafkaTemplate.class);
        ObjectMapper mapper = new ObjectMapper();
        KafkaProducerServiceImpl svc = new KafkaProducerServiceImpl(template, mapper);

        MailParams dto = new MailParams("id123", "to@x.com");

        svc.sendObjectMessage("topic.mail", dto);

        ArgumentCaptor<String> topic = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> payload = ArgumentCaptor.forClass(String.class);
        Mockito.verify(template).send(topic.capture(), payload.capture());

        Assertions.assertEquals("topic.mail", topic.getValue());

        var json = mapper.readTree(payload.getValue());
        Assertions.assertEquals("to@x.com", json.get("mailTo").asText());
        Assertions.assertEquals("id123", json.get("id").asText());
    }

}