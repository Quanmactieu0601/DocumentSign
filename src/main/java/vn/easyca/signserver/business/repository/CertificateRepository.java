package vn.easyca.signserver.business.repository;

import vn.easyca.signserver.business.domain.Certificate;

public interface CertificateRepository {

    Certificate getById(long id);

    Certificate getBySerial(String ownerId,String serial);

    Certificate save(Certificate certificate);

    Certificate getBySerial(String serial);

}
