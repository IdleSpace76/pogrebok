package ru.idles.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * @author a.zharov
 */
@Table(name = "bot_document")
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
public class BotDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "bot_document_seq", sequenceName = "bot_document_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    private String telegramId;
    private String docName;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private BinaryContent binaryContent;

    private String mimeType;
    private Long fileSize;

}
