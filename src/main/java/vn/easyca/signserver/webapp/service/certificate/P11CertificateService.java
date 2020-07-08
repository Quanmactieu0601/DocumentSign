package vn.easyca.signserver.webapp.service.certificate;

import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.repository.CertificateRepository;
import vn.easyca.signserver.webapp.service.dto.RegisterCertificateDto;
import vn.easyca.signserver.webapp.service.ex.CreateCertificateException;

public class P11CertificateService extends CertificateService {

    P11CertificateService(CertificateRepository certificateRepository) {
        super(certificateRepository);
    }

    @Override
    public Certificate createInstance(RegisterCertificateDto dto) throws CreateCertificateException {
        return null;
    }
}
