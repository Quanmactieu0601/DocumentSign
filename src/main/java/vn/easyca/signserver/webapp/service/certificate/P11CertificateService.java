package vn.easyca.signserver.webapp.service.certificate;
import vn.easyca.signserver.webapp.repository.CertificateRepository;
import vn.easyca.signserver.webapp.commond.encryption.Encryption;

public class P11CertificateService extends CertificateService {

    P11CertificateService(CertificateRepository certificateRepository, Encryption encryption) {
        super(certificateRepository,encryption);
    }


}
