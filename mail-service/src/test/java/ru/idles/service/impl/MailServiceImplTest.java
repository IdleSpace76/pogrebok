package ru.idles.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import ru.idles.dto.MailParams;

class MailServiceImplTest {

    @Test
    void buildsAndSendsMail_withIdPlaceholder() {
        JavaMailSender sender = Mockito.mock(JavaMailSender.class);
        MailServiceImpl svc = new MailServiceImpl(sender);

        ReflectionTestUtils.setField(svc, "emailFrom", "noreply@x.com");
        ReflectionTestUtils.setField(svc, "activationServiceUri", "https://app/activate?id={id}");

        svc.sendMail(new MailParams("id123", "to@x.com"));

        ArgumentCaptor<SimpleMailMessage> msg = ArgumentCaptor.forClass(SimpleMailMessage.class);
        Mockito.verify(sender).send(msg.capture());

        Assertions.assertArrayEquals(new String[]{"to@x.com"}, msg.getValue().getTo());
        Assertions.assertEquals("noreply@x.com", msg.getValue().getFrom());
        Assertions.assertTrue(msg.getValue().getSubject().contains("Активация"));

        String text = msg.getValue().getText();
        Assertions.assertTrue(text.contains("https://app/activate?id=id123"),
                () -> "Body должен содержать ссылку с id: " + text);
    }

    @Test
    void buildsAndSendsMail_withoutIdPlaceholder_doesNotInjectId() {
        JavaMailSender sender = Mockito.mock(JavaMailSender.class);
        MailServiceImpl svc = new MailServiceImpl(sender);

        ReflectionTestUtils.setField(svc, "emailFrom", "noreply@x.com");
        ReflectionTestUtils.setField(svc, "activationServiceUri", "https://app/activate");

        svc.sendMail(new MailParams("to@x.com", "id123"));

        ArgumentCaptor<SimpleMailMessage> msg = ArgumentCaptor.forClass(SimpleMailMessage.class);
        Mockito.verify(sender).send(msg.capture());

        String text = msg.getValue().getText();
        Assertions.assertTrue(text.contains("https://app/activate"));
        Assertions.assertFalse(text.contains("id123"),
                () -> "Если в URI нет {id}, id не должен подставляться: " + text);
    }
}

