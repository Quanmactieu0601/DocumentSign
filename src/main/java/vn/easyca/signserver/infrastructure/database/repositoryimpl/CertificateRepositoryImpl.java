package vn.easyca.signserver.infrastructure.database.repositoryimpl;

import org.springframework.stereotype.Component;
import vn.easyca.signserver.core.domain.Certificate;
import vn.easyca.signserver.core.repository.CertificateRepository;
import vn.easyca.signserver.infrastructure.database.repositoryimpl.utils.CertificateEncryptionHelper;
import vn.easyca.signserver.infrastructure.database.jpa.repository.CertificateJpaRepository;
import vn.easyca.signserver.infrastructure.database.jpa.entity.CertificateEntity;
import vn.easyca.signserver.infrastructure.database.repositoryimpl.mapper.CertificateMapper;

import java.util.List;
import java.util.Optional;

@Component
public class CertificateRepositoryImpl implements CertificateRepository {

    private final CertificateJpaRepository repository;

    private final CertificateMapper mapper = new CertificateMapper();

    private final CertificateEncryptionHelper encryptionHelper = new CertificateEncryptionHelper();

    public CertificateRepositoryImpl(CertificateJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Certificate getById(long id) {
        CertificateEntity entity = repository.getOne(id);
        return mapper.map(entity);
    }

    @Override
    public Certificate getBySerial(String ownerId, String serial) {
        Optional<CertificateEntity> entity = repository.getCertificateBySerial(serial);
        return entity.map(mapper::map).orElse(null);
    }

    @Override
    public Certificate save(Certificate certificate) {
        certificate = encryptionHelper.encryptCert(certificate);
        CertificateEntity entity = mapper.map(certificate);
        entity = repository.save(entity);
        return mapper.map(entity);
    }

    @Override
    public List<CertificateEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public List<CertificateEntity> getByOwnerId(String ownerId) {
        return repository.findByOwnerId(ownerId);
    }

    @Override
    public Certificate getBySerial(String serial) {
        Optional<CertificateEntity> entity = repository.getCertificateBySerial(serial);
        if (entity.isPresent()) {
            Certificate certificate = mapper.map(entity.get());
            certificate = encryptionHelper.decryptCert(certificate);
            return certificate;
        }
        return null;
    }

    @Override
    public boolean isExistCert(String serial) {
        try {
            Optional<CertificateEntity> entity = repository.getCertificateBySerial(serial);
            return entity.isPresent();
        } catch (Exception exception) {
            return false;
        }
    }
}
