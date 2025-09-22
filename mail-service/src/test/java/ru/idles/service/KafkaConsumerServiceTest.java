package ru.idles.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.idles.dto.MailParams;

import static org.mockito.ArgumentMatchers.any;

/**
 * @author a.zharov
 */
public class KafkaConsumerServiceTest {

    @Test
    void parsesJsonAndDelegates() throws Exception {
        MailService mail = Mockito.mock(MailService.class);
        ObjectMapper om = new ObjectMapper();
        var consumer = new KafkaConsumerService(om, mail);

        String payload = om.writeValueAsString(new MailParams("to@x.com", "id123"));
        consumer.listenRegistrationMail(payload);

        Mockito.verify(mail).sendMail(any(MailParams.class));
    }
}
