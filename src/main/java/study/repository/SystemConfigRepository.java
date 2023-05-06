package study.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.domain.SystemConfig;
import study.enums.SystemConfigKey;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {
    List<SystemConfig> findAllByActivatedIsTrueAndComId(Long comId);
    List<SystemConfig> findAllByActivatedIsTrue();
    Optional<SystemConfig> findByComIdAndKey(Long comId, SystemConfigKey key);
}
