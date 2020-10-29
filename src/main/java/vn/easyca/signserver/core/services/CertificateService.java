package vn.easyca.signserver.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.easyca.signserver.core.domain.Certificate;
import vn.easyca.signserver.core.exception.CertificateNotFoundAppException;
import vn.easyca.signserver.core.repository.CertificateRepository;
import vn.easyca.signserver.infrastructure.database.jpa.entity.CertificateEntity;
import vn.easyca.signserver.infrastructure.database.jpa.entity.UserEntity;
import vn.easyca.signserver.infrastructure.database.jpa.repository.CertificateJpaRepository;

import java.util.List;

@Service
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final CertificateJpaRepository certificateJpaRepository;
    public CertificateService(CertificateRepository certificateRepository, CertificateJpaRepository certificateJpaRepository) {
        this.certificateRepository = certificateRepository;
        this.certificateJpaRepository = certificateJpaRepository;
    }

    public Certificate save(Certificate certificate) {
        certificateRepository.save(certificate);
        return certificate;
    }

    public Certificate getById(long id) {
        return certificateRepository.getById(id);
    }

    public Certificate getBySerial(String serial) throws CertificateNotFoundAppException {
        Certificate certificate = certificateRepository.getBySerial(serial);
        if (certificate == null)
            throw new CertificateNotFoundAppException();
        return certificate;
    }

    @Transactional(readOnly = true)
    public Page<CertificateEntity> findAll(Pageable pageable) {
        return certificateJpaRepository.findAll(pageable);
    }

    @Transactional
    public void updateActiveStatus(long id) {
        Certificate certificate = certificateRepository.getById(id);
        if (certificate.getActiveStatus() == 1) {
            certificate.setActiveStatus(0);
        }
        else {
            certificate.setActiveStatus(1);
        }
        certificateRepository.save(certificate);
    }
    @Transactional(readOnly = true)
    public Page<CertificateEntity> findByFilter(Pageable pageable, String alias, String ownerId, String serial, String validDate, String expiredDate) {
        return certificateRepository.findByFilter(pageable, alias, ownerId, serial, validDate, expiredDate);
    }
}
