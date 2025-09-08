package ru.idles.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.idles.entity.BotImage;

/**
 * @author a.zharov
 */
@Repository
public interface BotImageRepository extends JpaRepository<BotImage, Long> {
}
