package vn.easyca.signserver.application.services;

import org.springframework.stereotype.Service;
import vn.easyca.signserver.application.domain.Certificate;
import vn.easyca.signserver.application.exception.CertificateNotFoundAppException;
import vn.easyca.signserver.application.repository.CertificateRepository;

@Service
public class CertificateService {

    private final CertificateRepository certificateRepository;

    public CertificateService(CertificateRepository certificateRepository) {
        this.certificateRepository = certificateRepository;
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
}
