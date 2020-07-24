package vn.easyca.signserver.webapp.service.certificate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.easyca.signserver.core.cryptotoken.Config;
import vn.easyca.signserver.core.cryptotoken.P12CryptoToken;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.domain.TokenInfo;
import vn.easyca.signserver.webapp.repository.CertificateRepository;
import vn.easyca.signserver.webapp.service.dto.ImportP12FileDTO;
import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class CertificateService {
    protected final Logger log = LoggerFactory.getLogger(Certificate.class);

    @Autowired
    private CertificateRepository certificateRepository;

    public Certificate save(Certificate certificate) {
        CertificateEncryptionHelper encryptionHelper = new CertificateEncryptionHelper();
        certificate = encryptionHelper.encryptCert(certificate);
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
        CertificateEncryptionHelper encryptionHelper = new CertificateEncryptionHelper();
        return encryptionHelper.decryptCert(optionalCertificate);
    }

    public Optional<Certificate> findBySerial(String serial) {
        Optional<Certificate> optionalCertificate = certificateRepository.getCertificateBySerial(serial);
        CertificateEncryptionHelper encryptionHelper = new CertificateEncryptionHelper();
        return encryptionHelper.decryptCert(optionalCertificate);
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

    public void importP12Certificate(ImportP12FileDTO dto) throws Exception {
        P12CryptoToken p12CryptoToken = new P12CryptoToken();
        Config config = new Config();
        byte[] fileContent = Base64.getDecoder().decode(dto.getP12Base64());
        config.initPkcs12(new ByteArrayInputStream(fileContent), dto.getPin());
        try {
            p12CryptoToken.init(config);
            List<String> aliases = p12CryptoToken.getAliases();
            String alias = aliases.get(0);
            X509Certificate x509Certificate = (X509Certificate) p12CryptoToken.getCertificate(alias);
            String serial = x509Certificate.getSerialNumber().toString(16);
            Certificate certificate = new Certificate();
            certificate.rawData(Base64.getEncoder().encodeToString(x509Certificate.getEncoded()));
            certificate.setOwnerId(dto.getOwnerId());
            certificate.setSerial(serial);
            certificate.setAlias(alias);
            certificate.tokenType(Certificate.PKCS_12);
            TokenInfo tokenInfo = new TokenInfo();
            tokenInfo.setData(dto.getP12Base64());
            certificate.setCertificateTokenInfo(tokenInfo);
            this.save(certificate);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not import p12 certificate");
        }
    }
}
