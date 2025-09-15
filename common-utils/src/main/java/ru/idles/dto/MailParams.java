package ru.idles.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author a.zharov
 */
@Data
@AllArgsConstructor
@Builder
public class MailParams {

    private String id;
    private String mailTo;
}
