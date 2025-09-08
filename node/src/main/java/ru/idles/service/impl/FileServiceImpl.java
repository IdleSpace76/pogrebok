package ru.idles.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.photo.PhotoSize;
import ru.idles.config.TelegramProperties;
import ru.idles.dao.BinaryContentRepository;
import ru.idles.dao.BotDocumentRepository;
import ru.idles.dao.BotImageRepository;
import ru.idles.dto.TgGetFileDto;
import ru.idles.entity.BinaryContent;
import ru.idles.entity.BotDocument;
import ru.idles.entity.BotImage;
import ru.idles.exception.UploadFileException;
import ru.idles.service.FileService;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * @author a.zharov
 */
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final TelegramProperties telegramProperties;
    private final BotDocumentRepository botDocumentRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BotImageRepository botImageRepository;
    private final WebClient webClient;

    @Override
    @Transactional
    public BotDocument processDoc(Message externalMessage) {
        Document telegramDoc = externalMessage.getDocument();
        String fileId = telegramDoc.getFileId();

        // Получаем file_path
        String filePath = fetchFilePath(fileId);

        // Качаем байты
        byte[] bytes = downloadFile(filePath);

        // Сохраняем бинарник
        BinaryContent binary = binaryContentRepository.save(
                BinaryContent.builder().fileAsBytes(bytes).build()
        );

        // Сохраняем метаданные
        BotDocument doc = BotDocument.builder()
                .telegramId(telegramDoc.getFileId())
                .docName(telegramDoc.getFileName())
                .binaryContent(binary)
                .mimeType(telegramDoc.getMimeType())
                .fileSize(telegramDoc.getFileSize())
                .build();

        return botDocumentRepository.save(doc);
    }

    @Override
    @Transactional
    public BotImage processImage(Message externalMessage) {
        // TODO обработка серии фото
        List<PhotoSize> imageList = externalMessage.getPhoto();

        PhotoSize bestResolution = imageList.stream()
                .max((a, b) -> {
                    int areaA = a.getWidth() * a.getHeight();
                    int areaB = b.getWidth() * b.getHeight();
                    return Integer.compare(areaA, areaB);
                })
                .orElseThrow(() -> new UploadFileException("Не удалось выбрать подходящий вариант фото"));

        String fileId = bestResolution.getFileId();

        // 1) получаем file_path
        String filePath = fetchFilePath(fileId);

        // 2) скачиваем байты
        byte[] bytes = downloadFile(filePath);

        // 3) готовим бинарник
        BinaryContent binary = BinaryContent.builder()
                .fileAsBytes(bytes)
                .build();

        // 4) сохраняем метаданные фото
        Integer size = bestResolution.getFileSize();

        BotImage image = BotImage.builder()
                .telegramId(fileId)
                .binaryContent(binary)
                .fileSize(size)
                .build();

        return botImageRepository.save(image);
    }

    private String fetchFilePath(String fileId) {
        URI uri = buildFileInfoUri(fileId);
        try {
            TgGetFileDto resp = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(TgGetFileDto.class)
                    .block();

            if (resp == null || !resp.isOk() || resp.getResult() == null || resp.getResult().getFilePath() == null) {
                throw new UploadFileException("Метод Telegram getFile вернул невалидный ответ: " + resp);
            }
            return resp.getResult().getFilePath();
        }
        catch (WebClientResponseException e) {
            throw new UploadFileException("Ошибка метода Telegram getFile: "
                    + e.getStatusCode().value() + " "
                    + e.getResponseBodyAsString(), e);
        }
    }

    private byte[] downloadFile(String filePath) {
        URI uri = buildFileStorageUri(filePath);
        try {
            byte[] body = webClient.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_OCTET_STREAM)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block(); // синхронно

            if (body == null || body.length == 0) {
                throw new UploadFileException("Пустая загрузка файла");
            }
            return body;
        }
        catch (WebClientResponseException e) {
            throw new UploadFileException("Неудачная попытка загрузки файла из Telegram: "
                    + e.getStatusCode().value(), e);
        }
    }

    private URI buildFileInfoUri(String fileId) {
        return new UriTemplate(telegramProperties.getFileInfoUri())
                .expand(Map.of("token", telegramProperties.getToken(), "fileId", fileId));
    }

    private URI buildFileStorageUri(String filePath) {
        return new UriTemplate(telegramProperties.getFileStorageUri())
                .expand(Map.of("token", telegramProperties.getToken(), "filePath", filePath));
    }
}
