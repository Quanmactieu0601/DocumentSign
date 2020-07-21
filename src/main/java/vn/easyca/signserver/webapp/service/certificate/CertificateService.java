package vn.easyca.signserver.webapp.service.certificate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.repository.CertificateRepository;
import vn.easyca.signserver.webapp.commond.encryption.Encryption;
import vn.easyca.signserver.webapp.service.dto.ImportCertificateDto;
import vn.easyca.signserver.webapp.service.error.CreateCertificateException;

import java.util.Optional;

public class CertificateService {

    protected final Logger log = LoggerFactory.getLogger(Certificate.class);

    protected final CertificateRepository certificateRepository;

    protected final EncryptionHelper encryptionHelper;

    CertificateService(CertificateRepository certificateRepository, Encryption encryption) {
        this.certificateRepository = certificateRepository;
        this.encryptionHelper = new EncryptionHelper(encryption);
    }


    /**
     * Save a certificate.
     *
     * @param certificate the entity to save.
     * @return the persisted entity.
     */
    public Certificate save(Certificate certificate) {
        Optional<Certificate> existCert = certificateRepository.getCertificateBySerial(certificate.getSerial());
        if (existCert.isPresent()) {
            this.delete(existCert.get().getId());
        }
        if (!certificate.isEncrypted()) {
            certificate = encryptionHelper.encryptCert(certificate);
        }
        certificateRepository.save(certificate);
        return certificate;
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
        Optional<Certificate> optionalCertificate = certificateRepository.findById(id);
        return optionalCertificate.isPresent() ? encryptionHelper.decryptCert(optionalCertificate) : optionalCertificate;
    }

    public Optional<Certificate> findBySerial(String serial) {
        Optional<Certificate> optionalCertificate = certificateRepository.getCertificateBySerial(serial);
        return optionalCertificate.isPresent() ? encryptionHelper.decryptCert(optionalCertificate) : optionalCertificate;
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


    public static class NotImplementedException extends Exception {
    }

}
