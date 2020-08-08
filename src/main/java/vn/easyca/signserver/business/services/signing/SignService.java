package vn.easyca.signserver.business.services.signing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.business.services.signing.dto.TokenInfoDTO;
import vn.easyca.signserver.business.services.signing.dto.request.SignRequest;
import vn.easyca.signserver.business.services.signing.dto.request.content.PDFSignContent;
import vn.easyca.signserver.business.services.signing.dto.request.content.RawSignContent;
import vn.easyca.signserver.business.services.signing.dto.request.content.XMLSignContent;
import vn.easyca.signserver.business.services.signing.dto.response.PDFSigningDataRes;
import vn.easyca.signserver.business.services.signing.dto.response.SignResultElement;
import vn.easyca.signserver.business.services.signing.dto.response.SigningDataResponse;
import vn.easyca.signserver.business.services.signing.signer.CryptoTokenProxyFactory;
import vn.easyca.signserver.business.services.signing.signer.CryptoTokenProxy;
import vn.easyca.signserver.business.services.signing.signer.RawSigner;
import vn.easyca.signserver.business.services.signing.signer.PDFSigner;
import vn.easyca.signserver.business.services.signing.signer.XmlSigner;

import java.util.HashMap;
import java.util.Map;

@Service
public class SignService {

    private final static String TEM_DIR = "./TemFile/";

    @Autowired
    private CryptoTokenProxyFactory cryptoTokenProxyFactory;

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

    public SigningDataResponse<String> signXML(SignRequest<XMLSignContent> request) throws Exception {
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
