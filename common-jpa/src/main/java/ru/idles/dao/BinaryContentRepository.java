package ru.idles.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.idles.entity.BinaryContent;

/**
 * @author a.zharov
 */
@Repository
public interface BinaryContentRepository extends JpaRepository<BinaryContent, Long> {
}
