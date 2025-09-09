package ru.idles.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.idles.entity.BotDocument;
import ru.idles.entity.BotImage;

import java.util.Optional;

/**
 * @author a.zharov
 */
@Repository
public interface BotImageRepository extends JpaRepository<BotImage, Long> {
    @Query("select d from BotImage d join fetch d.binaryContent where d.id = :id")
    Optional<BotImage> findWithBinaryById(@Param("id") Long id);
}
