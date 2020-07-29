package vn.easyca.signserver.webapp.adapter.repository;

import org.springframework.beans.factory.annotation.Autowired;
import vn.easyca.signserver.core.domain.Certificate;
import vn.easyca.signserver.core.repository.CertificateRepository;
import vn.easyca.signserver.webapp.persistence.jpa.CertificateJpaRepository;
import vn.easyca.signserver.webapp.persistence.entity.CertificateEntity;
import vn.easyca.signserver.webapp.persistence.mapper.CertificateMapper;

import java.util.Optional;

public class CertificateRepositoryImpl implements CertificateRepository {

    @Autowired
    private CertificateJpaRepository repository;

    @Autowired
    private CertificateMapper mapper;

    @Override
    public Certificate getById(long id) {
        CertificateEntity entity = repository.getOne(id);
        return  mapper.map(entity);
    }

    @Override
    public Certificate getBySerial(String ownerId, String serial) {
        Optional<CertificateEntity> entity =repository.getCertificateBySerial(serial);
        if (entity.isPresent())
            return mapper.map(entity.get());
        return null;
    }

    @Override
    public Certificate save(Certificate certificate) {
        CertificateEntity entity = mapper.map(certificate);
        entity = repository.save(entity);
        return mapper.map(entity);
    }

    @Override
    public Certificate getBySerial(String serial) {
        Optional<CertificateEntity> entity =repository.getCertificateBySerial(serial);
        if (entity.isPresent())
            return mapper.map(entity.get());
        return null;
    }
}
