package vn.easyca.signserver.webapp.adapter.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.easyca.signserver.core.domain.Certificate;
import vn.easyca.signserver.core.repository.CertificateRepository;
import vn.easyca.signserver.core.services.dto.CertificateGeneratedResult;
import vn.easyca.signserver.webapp.adapter.repository.utils.CertificateEncryptionHelper;
import vn.easyca.signserver.webapp.jpa.repository.CertificateJpaRepository;
import vn.easyca.signserver.webapp.jpa.entity.CertificateEntity;
import vn.easyca.signserver.webapp.adapter.repository.mapper.CertificateMapper;

import java.util.Optional;

@Component
public class CertificateRepositoryImpl implements CertificateRepository {

    @Autowired
    private CertificateJpaRepository repository;

    private final CertificateMapper mapper = new CertificateMapper();

    private final CertificateEncryptionHelper encryptionHelper = new CertificateEncryptionHelper();

    @Override
    public Certificate getById(long id) {
        CertificateEntity entity = repository.getOne(id);
        return mapper.map(entity);
    }

    @Override
    public Certificate getBySerial(String ownerId, String serial) {
        Optional<CertificateEntity> entity = repository.getCertificateBySerial(serial);
        return entity.map(certificateEntity -> mapper.map(certificateEntity)).orElse(null);
    }

    @Override
    public Certificate save(Certificate certificate) {
        certificate = encryptionHelper.encryptCert(certificate);
        CertificateEntity entity = mapper.map(certificate);
        entity = repository.save(entity);
        return mapper.map(entity);
    }

    @Override
    public Certificate getBySerial(String serial) {
        Certificate certificate = null;
        Optional<CertificateEntity> entity = repository.getCertificateBySerial(serial);
        if (entity.isPresent()) {
            certificate = mapper.map(entity.get());
            certificate = encryptionHelper.decryptCert(certificate);
            return certificate;
        }
        return null;
    }
}
