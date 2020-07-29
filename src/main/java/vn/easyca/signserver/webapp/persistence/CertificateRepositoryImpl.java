package vn.easyca.signserver.webapp.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.easyca.signserver.core.domain.Certificate;
import vn.easyca.signserver.core.repository.CertificateRepository;
import vn.easyca.signserver.webapp.domain.CertificateEntity;
import vn.easyca.signserver.webapp.jparepository.CertificateJpaRepository;

@Component
public class CertificateRepositoryImpl implements CertificateRepository {

    @Autowired
    private CertificateJpaRepository repository;

    private CertificateEncryptionHelper encryptionHelper;


    @Override
    public Certificate getById(long id) {
        CertificateEntity certificateEntity = repository.getOne(id);
        certificateEntity = encryptionHelper.decryptCert(certificateEntity);
        return map(certificateEntity);
    }

    @Override
    public Certificate getBySerial(String ownerId, String serial) {
        return null;
    }

    @Override
    public Certificate save(Certificate certificate) {
        return null;
    }

    @Override
    public Certificate getBySerial(String serial) {
        return null;
    }

    private Certificate map(CertificateEntity entity){
        Certificate certificate = new Certificate();
        certificate.setAlias(entity.getAlias());
        certificate.setRawData(entity.getRawData());
        certificate.setOwnerId(entity.getOwnerId());
        certificate.setSerial(entity.getSerial());
        certificate.setSubjectInfo(certificate.getSubjectInfo());
        return certificate;
    }
}
