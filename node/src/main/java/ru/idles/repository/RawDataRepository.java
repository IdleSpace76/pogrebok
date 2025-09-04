package ru.idles.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.idles.entity.RawData;

/**
 * @author a.zharov
 */
@Repository
public interface RawDataRepository extends JpaRepository<RawData, Long> {
}
