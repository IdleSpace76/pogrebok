package ru.idles.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.transaction.annotation.Transactional;
import ru.idles.entity.BotDocument;
import ru.idles.entity.BotImage;

/**
 * @author a.zharov
 */
public interface FileService {

    @Transactional(readOnly = true)
    ByteArrayResource getDocumentAsResource(Long id);

    @Transactional(readOnly = true)
    BotDocument getDocMeta(Long id);

    @Transactional(readOnly = true)
    ByteArrayResource getImageAsResource(Long id);

    @Transactional(readOnly = true)
    BotImage getImageMeta(Long id);
}
