package study.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import study.domain.CoreParser;

@Repository
public interface CoreParserRepository extends JpaRepository<CoreParser, Long>, JpaSpecificationExecutor<CoreParser> {
    Optional<CoreParser> findByName(String s);
}
