package vn.easyca.signserver.webapp.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.webapp.service.dto.*;
import vn.easyca.signserver.webapp.service.dto.request.SignPDFRequest;
import vn.easyca.signserver.webapp.service.dto.request.SignHashRequest;
import vn.easyca.signserver.webapp.service.dto.request.SignXMLRequest;
import vn.easyca.signserver.webapp.service.dto.response.PDFSignResponse;
import vn.easyca.signserver.webapp.service.dto.response.SignHashResponse;
import vn.easyca.signserver.webapp.service.model.hashsigner.HashSignResult;
import vn.easyca.signserver.webapp.service.model.hashsigner.HashSigner;
import vn.easyca.signserver.webapp.service.model.Signature;
import vn.easyca.signserver.webapp.service.model.pdfsigner.PDFSigner;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.repository.CertificateRepository;
import vn.easyca.signserver.webapp.service.model.xmlsigner.XmlSigner;

@Service
public class SignService {


    @Autowired
    private CertificateRepository certificateRepository;

    private final String temDir = "./TemFile/";

    public PDFSignResponse signPDFFile(SignPDFRequest request, SignatureInfoDto signatureInfoDto) throws Exception {

        PDFSigner pdfSigner = new PDFSigner(getSignature(signatureInfoDto),temDir);
        byte[] signedContent = pdfSigner.signPDF(request);
        pdfSigner.getSignatureInfo().setSignDate(request.getSignDate(),"yyyy-mm-dd hh:mm:ss");
        return new PDFSignResponse(signedContent);
    }

    public SignHashResponse signHash(SignHashRequest request, SignatureInfoDto signatureInfoDto) throws Exception {

        Signature signature = getSignature(signatureInfoDto);
        HashSignResult result = new HashSigner(signature).signHash(request.getBytes());
        return new SignHashResponse(result.getSignatureValue(),result.getCertificate());

    }

    public String signXML(SignXMLRequest request, SignatureInfoDto signatureInfoDto) throws Exception {

        Signature signature = getSignature(signatureInfoDto);
        XmlSigner xmlSigner = new XmlSigner(signature);
        return xmlSigner.sign(request);
    }


    private Signature getSignature(SignatureInfoDto signatureInfoDto) throws Exception {

        Certificate certificate =  certificateRepository.getCertificateBySerial(signatureInfoDto.getSerial());
        if (certificate == null)
            throw  new Exception("Certificate not found");
        return new Signature(certificate, signatureInfoDto.getPin());
    }
}
