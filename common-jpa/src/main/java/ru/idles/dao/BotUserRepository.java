package ru.idles.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.idles.entity.BotUser;

import java.util.Optional;

/**
 * @author a.zharov
 */
@Repository
public interface BotUserRepository extends JpaRepository<BotUser, Long> {
    Optional<BotUser> findBotUserByTelegramUserId(Long telegramUserId);
    Optional<BotUser> findByEmail(String email);
}
