package study.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.domain.SystemConfigCategory;

@Repository
public interface SystemConfigCategoryRepository extends JpaRepository<SystemConfigCategory, Long> {}
