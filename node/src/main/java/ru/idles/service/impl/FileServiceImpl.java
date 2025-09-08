package ru.idles.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.idles.config.TelegramProperties;
import ru.idles.dao.BinaryContentRepository;
import ru.idles.dao.BotDocumentRepository;
import ru.idles.entity.BinaryContent;
import ru.idles.entity.BotDocument;
import ru.idles.exception.UploadFileException;
import ru.idles.service.FileService;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author a.zharov
 */
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final TelegramProperties telegramProperties;
    private final BotDocumentRepository botDocumentRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BotDocument processFile(Message externalMessage) {
        Document telegramDoc = externalMessage.getDocument();
        String fileId = telegramDoc.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            BotDocument transientAppDoc = buildTransientAppDoc(telegramDoc, persistentBinaryContent);
            return botDocumentRepository.save(transientAppDoc);
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    private BotDocument buildTransientAppDoc(Document telegramDoc, BinaryContent persistentBinaryContent) {
        return BotDocument.builder()
                .telegramId(telegramDoc.getFileId())
                .docName(telegramDoc.getFileName())
                .binaryContent(persistentBinaryContent)
                .mimeType(telegramDoc.getMimeType())
                .fileSize(telegramDoc.getFileSize())
                .build();
    }

    private byte[] downloadFile(String filePath) {
        String fullUri = telegramProperties.getFileStorageUri().replace("{token}", telegramProperties.getToken())
                .replace("{filePath}", filePath);
        URL urlObj = null;
        try {
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new UploadFileException(e);
        }

        //TODO подумать над оптимизацией
        try (InputStream is = urlObj.openStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new UploadFileException(urlObj.toExternalForm(), e);
        }
    }

    private BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) {
        String filePath = getFilePath(response);
        byte[] fileInByte = downloadFile(filePath);
        BinaryContent transientBinaryContent = BinaryContent.builder()
                .fileAsArrayOfBytes(fileInByte)
                .build();
        return binaryContentRepository.save(transientBinaryContent);
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                telegramProperties.getFileInfoUri(),
                HttpMethod.GET,
                request,
                String.class,
                telegramProperties.getToken(),
                fileId
        );
    }
}
