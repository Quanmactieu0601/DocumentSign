package vn.easyca.signserver.business.services.sign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.business.domain.Certificate;
import vn.easyca.signserver.business.error.sign.BusinessError;
import vn.easyca.signserver.business.services.CertificateService;
import vn.easyca.signserver.business.services.sign.dto.request.SignatureVerificationRequest;
import vn.easyca.signserver.business.services.sign.dto.response.SignatureVerificationResponse;
import vn.easyca.signserver.business.services.sign.dto.TokenInfoDTO;
import vn.easyca.signserver.business.services.sign.dto.request.SignRequest;
import vn.easyca.signserver.business.services.sign.dto.request.content.PDFSignContent;
import vn.easyca.signserver.business.services.sign.dto.request.content.XMLSignContent;
import vn.easyca.signserver.business.services.sign.dto.response.PDFSigningDataRes;
import vn.easyca.signserver.business.services.sign.dto.response.SignDataResponse;
import vn.easyca.signserver.business.services.sign.signer.CryptoTokenProxyFactory;
import vn.easyca.signserver.business.services.sign.signer.CryptoTokenProxy;
import vn.easyca.signserver.business.services.sign.signer.RawSigner;
import vn.easyca.signserver.business.services.sign.signer.PDFSigner;
import vn.easyca.signserver.business.services.sign.signer.XmlSigner;
import vn.easyca.signserver.pki.sign.rawsign.SignatureValidator;

import java.security.cert.X509Certificate;

@Service
public class SignService {

    private final static String TEM_DIR = "./TemFile/";

    @Autowired
    private CryptoTokenProxyFactory cryptoTokenProxyFactory;
    @Autowired
    private CertificateService certificateService;

    public PDFSigningDataRes signPDFFile(SignRequest<PDFSignContent> request) throws Exception {
        try {
            TokenInfoDTO tokenInfoDTO = request.getTokenInfoDTO();
            CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(tokenInfoDTO.getSerial(), tokenInfoDTO.getPin());
            PDFSigner pdfSigner = new PDFSigner(cryptoTokenProxy, TEM_DIR);
            byte[] signedContent = pdfSigner.signPDF(request);
            return new PDFSigningDataRes(signedContent);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Can not sign pdf file");
        }
    }


    public Object signHash(SignRequest<String> request) throws Exception {
        try {
            TokenInfoDTO tokenInfoDTO = request.getTokenInfoDTO();
            CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(tokenInfoDTO.getSerial(), tokenInfoDTO.getPin());
            RawSigner rawSigner = new RawSigner(cryptoTokenProxy);
            return rawSigner.signHash(request);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Can not sign hash");
        }
    }

    public Object signRaw(SignRequest<String> request) throws Exception {
        try {
            TokenInfoDTO tokenInfoDTO = request.getTokenInfoDTO();
            CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(tokenInfoDTO.getSerial(), tokenInfoDTO.getPin());
            RawSigner rawSigner = new RawSigner(cryptoTokenProxy);
            return rawSigner.signRaw(request);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Can not sign batch hash");
        }
    }

    public SignDataResponse<String> signXML(SignRequest<XMLSignContent> request) throws Exception {
        try {
            TokenInfoDTO tokenInfoDTO = request.getTokenInfoDTO();
            CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(tokenInfoDTO.getSerial(), tokenInfoDTO.getPin());
            return new XmlSigner(cryptoTokenProxy).sign(request);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Can not sign Xml");
        }
    }



}
