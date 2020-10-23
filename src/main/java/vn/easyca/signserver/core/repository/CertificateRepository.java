package vn.easyca.signserver.core.repository;

import vn.easyca.signserver.core.domain.Certificate;

public interface CertificateRepository {

    Certificate getById(long id);

    Certificate getBySerial(String ownerId,String serial);

    Certificate save(Certificate certificate);

    Certificate getBySerial(String serial);

    boolean isExistCert(String serial);

}
