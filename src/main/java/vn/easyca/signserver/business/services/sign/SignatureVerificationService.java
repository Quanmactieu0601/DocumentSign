package vn.easyca.signserver.business.services.sign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.business.domain.Certificate;
import vn.easyca.signserver.business.error.sign.BusinessError;
import vn.easyca.signserver.business.services.CertificateService;
import vn.easyca.signserver.business.services.sign.dto.request.SignatureVerificationRequest;
import vn.easyca.signserver.business.services.sign.dto.response.SignatureVerificationResponse;
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
            if (certificate == null) {
                throw new BusinessError("Không tìm thấy chứng thư");
            }
            response.setCertificate(certificate.getRawData());
            X509Certificate x509Certificate = certificate.getX509Certificate();
            SignatureValidator rawValidator = new SignatureValidator();
            for (SignatureVerificationRequest.Element element : request.getElements()) {
                boolean result = rawValidator.verify(element.getOriginalData(), element.getSignature(), x509Certificate, request.getHashAlgorithm());
                response.add(element.getKey(), result);
            }
            return response;
        } catch (BusinessError businessError) {
            throw businessError;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Can not verify");
        }
    }

    public Object verifyRaw(SignatureVerificationRequest request) throws Exception {
        try {
            SignatureVerificationResponse response = new SignatureVerificationResponse();
            Certificate certificate = certificateService.getBySerial(request.getSerial());
            if (certificate == null) {
                throw new BusinessError("Không tìm thấy chứng thư");
            }
            response.setCertificate(certificate.getRawData());
            X509Certificate x509Certificate = certificate.getX509Certificate();
            SignatureValidator rawValidator = new SignatureValidator();
            for (SignatureVerificationRequest.Element element : request.getElements()) {
                boolean result = rawValidator.verify(element.getOriginalData(), element.getSignature(), x509Certificate);
                response.add(element.getKey(), result);
            }
            return response;
        } catch (BusinessError businessError) {
            throw businessError;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Can not verify");
        }
    }
}
