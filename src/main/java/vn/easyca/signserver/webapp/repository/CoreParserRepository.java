package vn.easyca.signserver.webapp.repository;

import vn.easyca.signserver.webapp.domain.CoreParser;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the CoreParser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CoreParserRepository extends JpaRepository<CoreParser, Long>, JpaSpecificationExecutor<CoreParser> {
    List<CoreParser> findAll();
}
