package ru.idles.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.idles.enums.UserState;

import java.time.Instant;

/**
 * Сущность пользователя
 *
 * @author a.zharov
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "bot_user")
public class BotUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bot_user_seq")
    @SequenceGenerator(name = "bot_user_seq", sequenceName = "bot_user_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    private Long telegramUserId;

    @CreationTimestamp
    private Instant createdAt;

    private String firstName;

    private String lastName;

    private String email;

    private String username;

    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    private UserState state;
}
