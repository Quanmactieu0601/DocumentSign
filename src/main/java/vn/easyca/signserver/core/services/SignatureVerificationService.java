package vn.easyca.signserver.core.services;

import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.domain.CertificateDTO;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.dto.SignatureVerificationRequest;
import vn.easyca.signserver.core.dto.SignatureVerificationResponse;
import vn.easyca.signserver.core.exception.CertificateAppException;
import vn.easyca.signserver.core.exception.CertificateNotFoundAppException;
import vn.easyca.signserver.core.exception.VerifiedAppException;
import vn.easyca.signserver.pki.sign.rawsign.SignatureValidator;
import vn.easyca.signserver.webapp.config.Constants;
import vn.easyca.signserver.webapp.service.CertificateService;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Service
public class SignatureVerificationService {

    private final CertificateService certificateService;

    public SignatureVerificationService(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    public Object verifyHash(SignatureVerificationRequest request) throws ApplicationException {
        SignatureVerificationResponse response = new SignatureVerificationResponse();
        CertificateDTO certificateDTO = certificateService.getBySerial(request.getSerial());
        if (certificateDTO == null)
            throw new CertificateNotFoundAppException();

        X509Certificate x509Certificate = certificateDTO.getX509Certificate();


        SignatureValidator rawValidator = new SignatureValidator();
        for (SignatureVerificationRequest.Element element : request.getElements()) {
            try {
                boolean result = rawValidator.verify(element.getOriginalData(), element.getSignature(), x509Certificate);
                response.add(element.getKey(), result);
            } catch (Exception exception) {
                throw new VerifiedAppException(exception);
            }
        }
        response.setCertificate(certificateDTO.getRawData());
        return response;
    }

    public SignatureVerificationResponse verifyRaw(SignatureVerificationRequest request) throws ApplicationException {
        if (request.getHashAlgorithm() == null)
            request.setHashAlgorithm(Constants.HASH_ALGORITHM.SHA1);
        SignatureVerificationResponse response = new SignatureVerificationResponse();
        CertificateDTO certificateDTO = certificateService.getBySerial(request.getSerial());
        if (certificateDTO == null)
            throw new CertificateNotFoundAppException();
        response.setCertificate(certificateDTO.getRawData());
        X509Certificate x509Certificate = certificateDTO.getX509Certificate();
        SignatureValidator rawValidator = new SignatureValidator();
        for (SignatureVerificationRequest.Element element : request.getElements()) {
            try {
                boolean result = rawValidator.verify(element.getOriginalData(), element.getSignature(), x509Certificate, request.getHashAlgorithm());
                response.add(element.getKey(), result);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return response;
    }
}
