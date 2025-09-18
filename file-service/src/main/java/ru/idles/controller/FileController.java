package ru.idles.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.idles.entity.BotDocument;
import ru.idles.service.FileService;
import ru.idles.utils.CryptoTool;

import java.nio.charset.StandardCharsets;

/**
 * Контроллер получения файлов
 *
 * @author a.zharov
 */
@RestController
@RequestMapping("/file")
@Slf4j
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final CryptoTool cryptoTool;


    @GetMapping("/doc")
    public ResponseEntity<ByteArrayResource> getDoc(@RequestParam("id") String id) {

        Long fileId = cryptoTool.idOf(id);

        BotDocument meta = fileService.getDocMeta(fileId);
        ByteArrayResource body = fileService.getDocumentAsResource(fileId);

        String filename = meta.getDocName() != null ? meta.getDocName() : "file";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(filename, StandardCharsets.UTF_8)
                                .build()
                                .toString())
                .contentLength(body.contentLength())
                .body(body);
    }

    @GetMapping("/image")
    public ResponseEntity<ByteArrayResource> getImage(@RequestParam("id") String id) {

        Long fileId = cryptoTool.idOf(id);

        ByteArrayResource body = fileService.getImageAsResource(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .build()
                                .toString())
                .contentLength(body.contentLength())
                .body(body);
    }
}
