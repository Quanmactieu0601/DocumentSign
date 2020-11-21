package vn.easyca.signserver.webapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.easyca.signserver.webapp.domain.Certificate;

public interface CertificateRepositoryCustom {
    Page<Certificate> findByFilter(Pageable pageable, String alias, String ownerId, String serial, String validDate, String expiredDate);
}
