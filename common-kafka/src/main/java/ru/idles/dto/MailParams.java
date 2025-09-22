package ru.idles.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ДТО с почтой пользователя
 *
 * @author a.zharov
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class MailParams {

    /// Зашифрованный id пользователя
    private String id;
    /// Почта пользователя
    private String mailTo;
}
