package vn.easyca.signserver.webapp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.easyca.signserver.core.domain.CertificateDTO;
import vn.easyca.signserver.core.exception.CertificateNotFoundAppException;
import vn.easyca.signserver.webapp.repository.CertificateRepository;
import vn.easyca.signserver.webapp.domain.Authority;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.domain.UserEntity;
import vn.easyca.signserver.webapp.repository.UserRepository;
import vn.easyca.signserver.webapp.service.mapper.CertificateMapper;
import vn.easyca.signserver.webapp.utils.CertificateEncryptionHelper;

import java.util.*;

@Service
public class CertificateService {

    private Optional<UserEntity> userEntityOptional;
    private List<Certificate> certificateList = new ArrayList<>();
    private final CertificateRepository certificateRepository;
    private final CertificateMapper mapper = new CertificateMapper();
    private final CertificateEncryptionHelper encryptionHelper = new CertificateEncryptionHelper();

    private final UserRepository userRepository;

    public CertificateService(CertificateRepository certificateRepository,  UserRepository userRepository) {
        this.certificateRepository = certificateRepository;
        this.userRepository = userRepository;
    }

    public List<Certificate> getByOwnerId(String ownerId) {
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
            certificateList = certificateRepository.findByOwnerId(ownerId);
        }
        return certificateList;
    }

    public CertificateDTO getBySerial(String serial) throws CertificateNotFoundAppException {
        Optional<Certificate> certificate = certificateRepository.findOneBySerial(serial);
        CertificateDTO certificateDTO = null;
        if (certificate.isPresent()) {
            certificateDTO = mapper.map(certificate.get());
            certificateDTO = encryptionHelper.decryptCert(certificateDTO);
        }
        if (certificateDTO == null)
            throw new CertificateNotFoundAppException();
        return certificateDTO;
    }

    @Transactional(readOnly = true)
    public Page<Certificate> findAll(Pageable pageable) {
        return certificateRepository.findAll(pageable);
    }

    @Transactional
    public void updateActiveStatus(long id) {
        Certificate certificate = certificateRepository.getOne(id);
        if (certificate.getActiveStatus() == 1) {
            certificate.setActiveStatus(0);
        } else {
            certificate.setActiveStatus(1);
        }
        certificateRepository.save(certificate);
    }

    @Transactional(readOnly = true)
    public Page<Certificate> findByFilter(Pageable pageable, String alias, String ownerId, String serial, String validDate, String expiredDate) {
        return certificateRepository.findByFilter(pageable, alias, ownerId, serial, validDate, expiredDate);
    }

    @Transactional
    public CertificateDTO save(CertificateDTO certificateDTO) {
        certificateDTO = encryptionHelper.encryptCert(certificateDTO);
        Certificate entity = mapper.map(certificateDTO);
        certificateRepository.save(entity);
        return mapper.map(entity);
    }
}
