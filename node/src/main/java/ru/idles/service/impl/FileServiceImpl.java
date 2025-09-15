package ru.idles.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
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
import ru.idles.enums.LinkType;
import ru.idles.exception.UploadFileException;
import ru.idles.service.FileService;
import ru.idles.utils.CryptoTool;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

/**
 * @author a.zharov
 */
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private static final long MAX_BYTES = 20L * 1024 * 1024;

    private final TelegramProperties telegramProperties;
    private final BotDocumentRepository botDocumentRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BotImageRepository botImageRepository;
    private final WebClient webClient;
    private final CryptoTool cryptoTool;

    @Value("${service.file.address}")
    private String linkAddress;

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

    @Override
    public String generateLink(Long fileId, LinkType linkType) {
        String hash = cryptoTool.hashOf(fileId);
        return "http://" + linkAddress + "/" + linkType + "?id=" + hash;
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

    // Если будут проблемы с памятью, попробовать сделать стрим данных прямо в бд
    private byte[] downloadFile(String filePath) {
        URI uri = buildFileStorageUri(filePath);
        Path tmp = null;
        try {
            tmp = Files.createTempFile("tg_", ".bin");

            DataBufferUtils.write(
                    webClient.get()
                            .uri(uri)
                            .accept(MediaType.APPLICATION_OCTET_STREAM)
                            .retrieve()
                            .bodyToFlux(DataBuffer.class),
                    tmp,
                    StandardOpenOption.WRITE
            ).block();

            long size = Files.size(tmp);
            if (size == 0) {
                throw new UploadFileException("Пустая загрузка файла");
            }
            if (size > MAX_BYTES) {
                throw new UploadFileException("Файл превышает 20 MB");
            }

            return Files.readAllBytes(tmp);
        }
        catch (WebClientResponseException e) {
            throw new UploadFileException("Неудачная попытка загрузки файла из Telegram: "
                    + e.getStatusCode().value(), e);
        }
        catch (IOException e) {
            throw new UploadFileException("Ошибка работы с временным файлом: " + e);
        }
        finally {
            if (tmp != null) {
                // Подчищаем за собой
                try {
                    Files.deleteIfExists(tmp);
                }
                catch (IOException ignored) {}
            }
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
