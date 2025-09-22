package ru.idles.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.idles.entity.BotDocument;
import ru.idles.service.FileService;
import ru.idles.utils.CryptoTool;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
class FileControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    FileService fileService;

    @MockitoBean
    CryptoTool cryptoTool;

    @Test
    void getImage_ok() throws Exception {
        String externalId = "abc";
        long internalId = 123L;
        byte[] bytes = {1, 2, 3};

        Mockito.when(cryptoTool.idOf(externalId)).thenReturn(internalId);
        Mockito.when(fileService.getImageAsResource(internalId))
                .thenReturn(new ByteArrayResource(bytes));

        mvc.perform(get("/file/image").param("id", externalId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment"))
                .andExpect(header().longValue(HttpHeaders.CONTENT_LENGTH, bytes.length))
                .andExpect(content().bytes(bytes));
    }

    @Test
    void getDoc_ok_withFileName() throws Exception {
        String externalId = "xyz";
        long internalId = 55L;
        byte[] bytes = {9, 8, 7};

        BotDocument meta = new BotDocument();
        meta.setId(internalId);
        meta.setDocName("report.pdf");

        Mockito.when(cryptoTool.idOf(externalId)).thenReturn(internalId);
        Mockito.when(fileService.getDocMeta(internalId)).thenReturn(meta);
        Mockito.when(fileService.getDocumentAsResource(internalId))
                .thenReturn(new ByteArrayResource(bytes));

        mvc.perform(get("/file/doc").param("id", externalId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, org.hamcrest.Matchers.containsString("attachment")))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, org.hamcrest.Matchers.containsString("report.pdf")))
                .andExpect(header().longValue(HttpHeaders.CONTENT_LENGTH, bytes.length))
                .andExpect(content().bytes(bytes));
    }
}
