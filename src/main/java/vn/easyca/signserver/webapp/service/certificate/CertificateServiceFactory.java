package vn.easyca.signserver.webapp.service.certificate;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.webapp.domain.CertificateType;
import vn.easyca.signserver.webapp.service.Encryption;
import vn.easyca.signserver.webapp.repository.CertificateRepository;

@Service
public class CertificateServiceFactory {

    private Encryption encryption;

    private CertificateRepository repository;

    public CertificateServiceFactory(Encryption encryption, CertificateRepository repository) {
        this.encryption = encryption;
        this.repository = repository;
    }

    public CertificateService getService(CertificateType type) {

        if (type == CertificateType.PKCS12)
            return new P12CertificateService(repository, encryption);
        else if (type == CertificateType.PKCS11)
            return new P11CertificateService(repository);
        return new CertificateService(repository);
    }

    public CertificateService getService(String type) {

        CertificateType eType = CertificateType.getType(type);
        return getService(eType);
    }

}
