package ru.idles.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author a.zharov
 */
@Data
@Builder
public class NodeMsgDto {
    private String chatId;
    private String text;
}
