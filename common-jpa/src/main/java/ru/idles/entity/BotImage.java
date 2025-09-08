package ru.idles.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * @author a.zharov
 */
@Table(name = "bot_image")
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
public class BotImage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "bot_image_seq", sequenceName = "bot_image_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    private String telegramId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "binary_content_id")
    private BinaryContent binaryContent;

    private Integer fileSize;
}
