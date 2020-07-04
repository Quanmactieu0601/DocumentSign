package vn.easyca.signserver.webapp.service;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.webapp.service.dto.PDFSignRequest;
import vn.easyca.signserver.webapp.service.dto.PDFSignResponse;
import vn.easyca.signserver.webapp.service.dto.SignHashRequest;
import vn.easyca.signserver.webapp.service.dto.SignHashResponse;
import vn.easyca.signserver.webapp.service.model.hashsigner.HashSignResult;
import vn.easyca.signserver.webapp.service.model.hashsigner.HashSigner;
import vn.easyca.signserver.webapp.service.model.Signature;
import vn.easyca.signserver.webapp.service.model.pdfsigner.PDFSigner;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.repository.CertificateRepository;

@Service
public class SignService {


    private CertificateRepository certificateRepository;

    private final String temDir = "./TemFile/";

    public PDFSignResponse signPDFFile(PDFSignRequest request) throws Exception {

        PDFSigner pdfSigner = new PDFSigner(getSignature(request.getSerial(),request.getPin()),temDir);
        byte[] signedContent = pdfSigner.signPDF(request);
        pdfSigner.getSignatureInfo().setSignDate(request.getSignDate(),"yyyy-mm-dd hh:mm:ss");
        return new PDFSignResponse(signedContent);
    }

    public SignHashResponse signHash(SignHashRequest request) throws Exception {

        Signature signature = getSignature(request.getSerial(),request.getPin());
        HashSignResult result = new HashSigner(signature).signHash(request.getBytes());
        return new SignHashResponse(result.getSignatureValue(),result.getCertificate());

    }

    private Signature getSignature(String serial,String pin) throws Exception {
        Certificate  certificate= new Certificate();
        certificate.setAlias("CÔNG TY CỔ PHẦN ĐẦU TƯ CÔNG NGHỆ VÀ THƯƠNG MẠI SOFTDREAMS's Viettel Group ID");
        certificate.setRawData("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");
        return new Signature(certificate,pin);
    }
}
