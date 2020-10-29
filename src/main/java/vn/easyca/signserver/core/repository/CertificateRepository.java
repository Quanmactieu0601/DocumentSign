package vn.easyca.signserver.core.repository;

import vn.easyca.signserver.core.domain.Certificate;
import vn.easyca.signserver.infrastructure.database.jpa.entity.CertificateEntity;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository {

    List<CertificateEntity> findAll();
    Certificate getById(long id);

    List<CertificateEntity> getByOwnerId(String ownerId);

    Certificate getBySerial(String ownerId,String serial);

    Certificate save(Certificate certificate);

    Certificate getBySerial(String serial);

    boolean isExistCert(String serial);

}
