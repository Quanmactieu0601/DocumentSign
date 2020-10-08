package vn.easyca.signserver.application.repository;

import vn.easyca.signserver.application.domain.Certificate;

public interface CertificateRepository {

    Certificate getById(long id);

    Certificate getBySerial(String ownerId,String serial);

    Certificate save(Certificate certificate);

    Certificate getBySerial(String serial);

}
