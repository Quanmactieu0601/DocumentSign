package vn.easyca.signserver.application.services;

import org.springframework.stereotype.Service;
import vn.easyca.signserver.application.domain.Certificate;
import vn.easyca.signserver.application.exception.ApplicationException;
import vn.easyca.signserver.application.dto.SignatureVerificationRequest;
import vn.easyca.signserver.application.dto.SignatureVerificationResponse;
import vn.easyca.signserver.application.exception.CertificateAppException;
import vn.easyca.signserver.application.exception.CertificateNotFoundAppException;
import vn.easyca.signserver.application.exception.VerifiedAppException;
import vn.easyca.signserver.pki.sign.rawsign.SignatureValidator;

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
        Certificate certificate = certificateService.getBySerial(request.getSerial());
        if (certificate == null)
            throw new CertificateNotFoundAppException();

        X509Certificate x509Certificate = null;
        try {
            x509Certificate = certificate.getX509Certificate();
        } catch (CertificateException e) {
            throw new CertificateAppException(e);
        }

        SignatureValidator rawValidator = new SignatureValidator();
        for (SignatureVerificationRequest.Element element : request.getElements()) {
            try {
                boolean result = rawValidator.verify(element.getOriginalData(), element.getSignature(), x509Certificate);
                response.add(element.getKey(), result);
            } catch (Exception exception) {
                throw new VerifiedAppException(exception);
            }
        }
        response.setCertificate(certificate.getRawData());
        return response;
    }

    public SignatureVerificationResponse verifyRaw(SignatureVerificationRequest request) throws ApplicationException {
        if (request.getHashAlgorithm() == null)
            request.setHashAlgorithm("sha1");
        SignatureVerificationResponse response = new SignatureVerificationResponse();
        Certificate certificate = certificateService.getBySerial(request.getSerial());
        if (certificate == null)
            throw new CertificateNotFoundAppException();
        response.setCertificate(certificate.getRawData());
        X509Certificate x509Certificate = null;
        try {
            x509Certificate = certificate.getX509Certificate();
        } catch (CertificateException e) {
            throw new CertificateAppException(e);
        }
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
