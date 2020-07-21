package vn.easyca.signserver.webapp.service.certificate;

import org.springframework.stereotype.Service;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.commond.encryption.Encryption;
import vn.easyca.signserver.webapp.repository.CertificateRepository;

@Service
public class CertificateServiceFactory {

    private Encryption encryption;

    private CertificateRepository repository;

    public CertificateServiceFactory(Encryption encryption, CertificateRepository repository) {
        this.encryption = encryption;
        this.repository = repository;
    }

    public CertificateService getService(String type) {

        if (type == Certificate.PKCS_11)
            return new P11CertificateService(repository, encryption);
        else if (type == Certificate.PKCS_12)
            return new P12CertificateService(repository, encryption);
        return new CertificateService(repository, encryption);
    }

    public CertificateService getService() {
        return getService(null);
    }
}
