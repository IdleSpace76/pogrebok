package ru.idles.service.impl;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.idles.config.KafkaTopicsProperties;
import ru.idles.dao.BotUserRepository;
import ru.idles.entity.BotImage;
import ru.idles.entity.BotUser;
import ru.idles.enums.LinkType;
import ru.idles.enums.UserState;
import ru.idles.service.BotUserService;
import ru.idles.service.FileService;
import ru.idles.service.KafkaProducerService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class NodeServiceImplTest {

    @Test
    void buildsLinkAndCallsFileService_onImage() {
        KafkaProducerService kafka = mock(KafkaProducerService.class);
        KafkaTopicsProperties topics = mock(KafkaTopicsProperties.class);
        BotUserRepository users = mock(BotUserRepository.class);
        FileService files = mock(FileService.class);
        BotUserService userSvc = mock(BotUserService.class);

        when(topics.getNodeMessages()).thenReturn("node-topic");

        BotUser u = BotUser.builder()
                .id(1L)
                .telegramUserId(777L)
                .isActive(true)
                .state(UserState.BASIC_STATE)
                .build();
        when(users.findBotUserByTelegramUserId(777L)).thenReturn(Optional.of(u));

        BotImage img = new BotImage();
        img.setId(42L);
        when(files.processImage(any(Message.class))).thenReturn(img);
        when(files.generateLink(42L, LinkType.IMAGE)).thenReturn("http://host/file/image?id=abc");

        NodeServiceImpl svc = new NodeServiceImpl(kafka, topics, users, files, userSvc);

        Message msg = mock(Message.class);
        when(msg.hasPhoto()).thenReturn(true);
        when(msg.hasText()).thenReturn(false);
        when(msg.hasDocument()).thenReturn(false);
        when(msg.getChatId()).thenReturn(12345L);

        User tgUser = mock(User.class);
        when(tgUser.getId()).thenReturn(777L);
        when(msg.getFrom()).thenReturn(tgUser);

        Update upd = mock(Update.class);
        when(upd.getMessage()).thenReturn(msg);

        svc.processMsg(upd);

        verify(files).processImage(msg);
        verify(files).generateLink(42L, LinkType.IMAGE);

        ArgumentCaptor<Object> payload = ArgumentCaptor.forClass(Object.class);
        verify(kafka).sendObjectMessage(eq("node-topic"), payload.capture());

        String text = payload.getValue().toString();
        assertTrue(text.contains("http://host/file/image?id=abc"), "Ответ должен содержать ссылку");
    }
}
