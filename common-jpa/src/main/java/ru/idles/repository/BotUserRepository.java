package ru.idles.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.idles.entity.BotUser;

/**
 * @author a.zharov
 */
@Repository
public interface BotUserRepository extends JpaRepository<BotUser, Long> {
    BotUser findBotUserByTelegramUserId(Long telegramUserId);
}
