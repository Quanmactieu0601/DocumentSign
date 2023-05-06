package study.repository;

import java.util.Optional;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import study.domain.CertPackage;

@Repository
public interface CertPackageRepository extends R2dbcRepository<CertPackage, Long> {
    Optional<CertPackage> findOneByPackageCode(String packageCode);
}
