package ru.idles.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.idles.dao.BotDocumentRepository;
import ru.idles.dao.BotImageRepository;
import ru.idles.entity.BotDocument;
import ru.idles.entity.BotImage;
import ru.idles.exception.NotFoundException;
import ru.idles.service.FileService;

/**
 * @author a.zharov
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final BotDocumentRepository botDocumentRepository;
    private final BotImageRepository botImageRepository;

    @Transactional(readOnly = true)
    @Override
    public ByteArrayResource getDocumentAsResource(Long id) {
        BotDocument doc = botDocumentRepository.findWithBinaryById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Документ [%s] не найден.", id)));
        byte[] bytes = doc.getBinaryContent().getFileAsBytes();
        return new ByteArrayResource(bytes);
    }

    @Transactional(readOnly = true)
    @Override
    public BotDocument getDocMeta(Long id) {
        return botDocumentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Документ [%s] не найден.", id)));
    }

    @Transactional(readOnly = true)
    @Override
    public ByteArrayResource getImageAsResource(Long id) {
        BotImage doc = botImageRepository.findWithBinaryById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Изображение [%s] не найден.", id)));
        byte[] bytes = doc.getBinaryContent().getFileAsBytes();
        return new ByteArrayResource(bytes);
    }

    @Transactional(readOnly = true)
    @Override
    public BotImage getImageMeta(Long id) {
        return botImageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Изображение [%s] не найдено.", id)));
    }
}
