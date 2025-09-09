package ru.idles.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * @author a.zharov
 */
@Table(name = "binary_content")
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
public class BinaryContent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "binary_content_seq")
    @SequenceGenerator(name = "binary_content_seq", sequenceName = "binary_content_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(columnDefinition = "bytea")
    private byte[] fileAsBytes;
}
