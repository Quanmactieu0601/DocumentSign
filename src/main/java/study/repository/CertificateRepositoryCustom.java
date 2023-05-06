package study.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import study.domain.Certificate;

public interface CertificateRepositoryCustom {
    Page<Certificate> findByFilter(
        Pageable pageable,
        String alias,
        String ownerId,
        String serial,
        String validDate,
        String expiredDate,
        Integer type
    );
}
