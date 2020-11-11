package vn.easyca.signserver.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.easyca.signserver.core.domain.Certificate;
import vn.easyca.signserver.core.exception.CertificateNotFoundAppException;
import vn.easyca.signserver.core.repository.CertificateRepository;
import vn.easyca.signserver.infrastructure.database.jpa.entity.Authority;
import vn.easyca.signserver.infrastructure.database.jpa.entity.CertificateEntity;
import vn.easyca.signserver.infrastructure.database.jpa.entity.UserEntity;
import vn.easyca.signserver.infrastructure.database.jpa.repository.CertificateJpaRepository;
import vn.easyca.signserver.infrastructure.database.jpa.repository.UserRepository;
import vn.easyca.signserver.infrastructure.database.repositoryimpl.UserRepositoryImpl;

import java.util.*;

@Service
public class CertificateService {

    private Optional<UserEntity> userEntityOptional;
    private List<CertificateEntity> certificateList = new ArrayList<>();
    private final CertificateRepository certificateRepository;
    private final CertificateJpaRepository certificateJpaRepository;

    private final UserRepository userRepository;

    public CertificateService(CertificateRepository certificateRepository, CertificateJpaRepository certificateJpaRepository, UserRepository userRepository) {
        this.certificateRepository = certificateRepository;
        this.certificateJpaRepository = certificateJpaRepository;
        this.userRepository = userRepository;
    }

    public Certificate save(Certificate certificate) {
        certificateRepository.save(certificate);
        return certificate;
    }

    public Certificate getById(long id) {
        return certificateRepository.getById(id);
    }

    public List<CertificateEntity> getByOwnerId(String ownerId) {
        Long id = Long.parseLong(ownerId);
        userEntityOptional = userRepository.findById(id);
        System.out.println(userEntityOptional);
        UserEntity userEntity = userEntityOptional.get();
        boolean roleAdmin = false;
        Set<Authority> userAuthority = userEntity.getAuthorities();
        for (Authority setAuthority : userAuthority) {
            if (setAuthority.getName().equals("ROLE_ADMIN")) {
                roleAdmin = true;
            }
        }
        if (roleAdmin) {
            certificateList = certificateRepository.findAll();
        } else {
            certificateList = certificateRepository.getByOwnerId(ownerId);
        }
        return certificateList;
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
        } else {
            certificate.setActiveStatus(1);
        }
        certificateRepository.save(certificate);
    }

    @Transactional(readOnly = true)
    public Page<CertificateEntity> findByFilter(Pageable pageable, String alias, String ownerId, String serial, String validDate, String expiredDate) {
        return certificateRepository.findByFilter(pageable, alias, ownerId, serial, validDate, expiredDate);
    }
}
