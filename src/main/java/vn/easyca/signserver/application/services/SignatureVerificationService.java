package vn.easyca.signserver.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.application.domain.Certificate;
import vn.easyca.signserver.application.exception.ApplicationException;
import vn.easyca.signserver.application.dto.SignatureVerificationRequest;
import vn.easyca.signserver.application.dto.SignatureVerificationResponse;
import vn.easyca.signserver.pki.sign.rawsign.SignatureValidator;

import java.security.cert.X509Certificate;

@Service
public class SignatureVerificationService {

    @Autowired
    private CertificateService certificateService;

    public Object verifyHash(SignatureVerificationRequest request) throws Exception {
        try {
            SignatureVerificationResponse response = new SignatureVerificationResponse();
            Certificate certificate = certificateService.getBySerial(request.getSerial());
            if (certificate == null)
                throw new ApplicationException("Không tìm thấy chứng thư");
            response.setCertificate(certificate.getRawData());
            X509Certificate x509Certificate = certificate.getX509Certificate();
            SignatureValidator rawValidator = new SignatureValidator();
            for (SignatureVerificationRequest.Element element : request.getElements()) {
                boolean result = rawValidator.verify(element.getOriginalData(), element.getSignature(), x509Certificate);
                response.add(element.getKey(), result);
            }
            return response;
        } catch (ApplicationException applicationException) {
            throw applicationException;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Can not verify");
        }
    }

    public SignatureVerificationResponse verifyRaw(SignatureVerificationRequest request) throws Exception {
        try {
            if (request.getHashAlgorithm() == null)
                request.setHashAlgorithm("sha1");
            SignatureVerificationResponse response = new SignatureVerificationResponse();
            Certificate certificate = certificateService.getBySerial(request.getSerial());
            if (certificate == null)
                throw new ApplicationException("Không tìm thấy chứng thư");
            response.setCertificate(certificate.getRawData());
            X509Certificate x509Certificate = certificate.getX509Certificate();
            SignatureValidator rawValidator = new SignatureValidator();
            for (SignatureVerificationRequest.Element element : request.getElements()) {
                boolean result = rawValidator.verify(element.getOriginalData(), element.getSignature(), x509Certificate,request.getHashAlgorithm());
                response.add(element.getKey(), result);
            }
            return response;
        } catch (ApplicationException applicationException) {
            throw applicationException;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Can not verify");
        }
    }
}
