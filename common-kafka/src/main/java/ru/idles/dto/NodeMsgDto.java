package ru.idles.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Сообщение от ноды
 *
 * @author a.zharov
 */
@Data
@Builder
public class NodeMsgDto {
    /// Id чата получателя
    private String chatId;
    /// Сообщение пользователю
    private String text;
}
