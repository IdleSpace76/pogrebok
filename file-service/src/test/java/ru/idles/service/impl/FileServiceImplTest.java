package ru.idles.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ByteArrayResource;
import ru.idles.dao.BotDocumentRepository;
import ru.idles.dao.BotImageRepository;
import ru.idles.entity.BotImage;
import ru.idles.entity.BinaryContent;

import java.util.Optional;

class FileServiceImplTest {

    @Test
    void returnsImageBytes() {
        BotDocumentRepository docRepo = Mockito.mock(BotDocumentRepository.class);
        BotImageRepository imageRepo = Mockito.mock(BotImageRepository.class);

        BotImage image = Mockito.mock(BotImage.class);
        BinaryContent binary = Mockito.mock(BinaryContent.class);
        byte[] expected = new byte[]{1, 2, 3};

        Mockito.when(image.getBinaryContent()).thenReturn(binary);
        Mockito.when(binary.getFileAsBytes()).thenReturn(expected);
        Mockito.when(imageRepo.findWithBinaryById(123L)).thenReturn(Optional.of(image));

        FileServiceImpl svc = new FileServiceImpl(docRepo, imageRepo);

        ByteArrayResource res = svc.getImageAsResource(123L);

        Assertions.assertNotNull(res);
        Assertions.assertArrayEquals(expected, res.getByteArray());
        Assertions.assertEquals(expected.length, res.contentLength());
    }
}
