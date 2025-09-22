package ru.idles.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.idles.config.KafkaTopicsProperties;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BotUpdateHandlerTest {

    @Mock
    OkHttpTelegramClient telegramClient;
    @Mock
    KafkaProducerService kafkaProducerService;
    @Mock
    KafkaTopicsProperties kafkaTopicsProperties;

    @InjectMocks
    BotUpdateHandler handler;

    @Test
    void handlesStartCommand_sendsToKafka() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        User user = mock(User.class);

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.getFrom()).thenReturn(user);
        when(user.getUserName()).thenReturn("tester");
        when(message.getText()).thenReturn("/start");

        when(kafkaTopicsProperties.getUserMessages()).thenReturn("user-messages");

        handler.handleUpdate(update);

        verify(kafkaProducerService).sendObjectMessage("user-messages", update);
    }

    @Test
    void sendAnswerMsg_callsTelegramClient() throws Exception {
        SendMessage msg = new SendMessage("12345", "hello");

        handler.sendAnswerMsg(msg);

        verify(telegramClient).execute(msg);
    }

    @Test
    void sendAnswerMsg_doesNotThrowOnTelegramException() throws Exception {
        SendMessage msg = new SendMessage("12345", "hello");
        doThrow(new TelegramApiException("boom")).when(telegramClient).execute(any(SendMessage.class));

        handler.sendAnswerMsg(msg);

        verify(telegramClient).execute(msg);
    }
}

