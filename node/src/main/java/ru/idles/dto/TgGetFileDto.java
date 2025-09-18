package ru.idles.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.File;

/**
 * Дто с информацией ТГ документа
 *
 * @author a.zharov
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TgGetFileDto {
    private boolean ok;
    private File result;
    private Integer error_code;
    private String description;
}
