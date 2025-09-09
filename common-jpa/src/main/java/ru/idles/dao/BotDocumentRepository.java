package ru.idles.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.idles.entity.BotDocument;

import java.util.Optional;

/**
 * @author a.zharov
 */
@Repository
public interface BotDocumentRepository extends JpaRepository<BotDocument, Long> {
    @Query("select d from BotDocument d join fetch d.binaryContent where d.id = :id")
    Optional<BotDocument> findWithBinaryById(@Param("id") Long id);
}
