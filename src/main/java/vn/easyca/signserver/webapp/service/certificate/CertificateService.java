package vn.easyca.signserver.webapp.service.certificate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.domain.TokenInfo;
import vn.easyca.signserver.webapp.repository.CertificateRepository;
import vn.easyca.signserver.webapp.service.Encryption;
import vn.easyca.signserver.webapp.service.dto.CertificateGeneratorDto;
import vn.easyca.signserver.webapp.service.dto.ImportCertificateDto;
import vn.easyca.signserver.webapp.service.dto.NewCertificateInfo;
import vn.easyca.signserver.webapp.service.error.CreateCertificateException;
import vn.easyca.signserver.webapp.service.error.GenCertificateInputException;

import java.util.Optional;

public class CertificateService {

    protected final Logger log = LoggerFactory.getLogger(Certificate.class);

    protected final CertificateRepository certificateRepository;

    protected final Encryption encryption;

    CertificateService(CertificateRepository certificateRepository, Encryption encryption) {
        this.certificateRepository = certificateRepository;
        this.encryption = encryption;
    }


    /**
     * Save a certificate.
     *
     * @param certificate the entity to save.
     * @return the persisted entity.
     */
    public Certificate save(Certificate certificate) {
        log.debug("Request to save Certificate : {}", certificate);
        Certificate existCert = certificateRepository.getCertificateBySerial(certificate.getSerial());
        if (existCert != null) {
            certificate.isExtensionCert(existCert);
            certificateRepository.delete(existCert);
        }
        return certificateRepository.save(certificate);
    }

    public Certificate saveWithEncryption(Certificate certificate) throws Encryption.EncryptionException {
        if (encryption != null) {
            certificate.setTokenInfo(encryption.encrypt(certificate.getTokenInfo()));
        }
        return save(certificate);
    }

    protected TokenInfo encryptionTokenInfo(TokenInfo tokenInfo) throws Encryption.EncryptionException {
        return tokenInfo.setData(encryption.encrypt(tokenInfo.getData()));
    }
    /**
     * Get all the certificates.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Certificate> findAll(Pageable pageable) {
        log.debug("Request to get all Certificates");
        return certificateRepository.findAll(pageable);
    }


    /**
     * Get one certificate by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Certificate> findOne(Long id) {
        log.debug("Request to get Certificate : {}", id);
        return certificateRepository.findById(id);
    }

    public Certificate findBySerial(String serial) {
        return certificateRepository.getCertificateBySerial(serial);
    }

    /**
     * Delete the certificate by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Certificate : {}", id);
        certificateRepository.deleteById(id);
    }

    public Certificate importCertificate(ImportCertificateDto dto) throws NotImplementedException, CreateCertificateException {


        throw new NotImplementedException();
    }

    public NewCertificateInfo genCertificate(CertificateGeneratorDto dto) throws NotImplementedException, CreateCertificateException, GenCertificateInputException {
        throw new NotImplementedException();
    }


    public static class NotImplementedException extends Exception {
    }
}
