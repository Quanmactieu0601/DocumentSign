package vn.easyca.signserver.business.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.business.domain.Certificate;
import vn.easyca.signserver.business.repository.CertificateRepository;

@Service
public class CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    public Certificate save(Certificate certificate) {
        certificateRepository.save(certificate);
        return certificate;
    }

    public Certificate getById(long id) {
        return certificateRepository.getById(id);
    }

    public Certificate getBySerial(String serial) {
        return certificateRepository.getBySerial(serial);
    }
}
