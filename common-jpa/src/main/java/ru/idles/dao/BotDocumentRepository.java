package ru.idles.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.idles.entity.BotDocument;

/**
 * @author a.zharov
 */
@Repository
public interface BotDocumentRepository extends JpaRepository<BotDocument, Long> {
}
