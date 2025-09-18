package ru.idles.enums;

import lombok.RequiredArgsConstructor;

/**
 * Ссылка на тип файла
 *
 * @author a.zharov
 */
@RequiredArgsConstructor
public enum LinkType {
    DOC("file/doc"),
    IMAGE("file/image");

    private final String link;

    @Override
    public String toString() {
        return this.link;
    }
}
