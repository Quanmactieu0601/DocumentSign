package vn.easyca.signserver.core.services.signing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.services.signing.dto.TokenInfoDTO;
import vn.easyca.signserver.core.services.signing.dto.request.SigningRequest;
import vn.easyca.signserver.core.services.signing.dto.request.content.PDFSigningContent;
import vn.easyca.signserver.core.services.signing.dto.request.content.RawSigningContent;
import vn.easyca.signserver.core.services.signing.dto.request.content.XMLSigningContent;
import vn.easyca.signserver.core.services.signing.dto.response.PDFSigningDataRes;
import vn.easyca.signserver.core.services.signing.dto.response.SigningDataResponse;
import vn.easyca.signserver.core.services.signing.signer.CryptoTokenProxyFactory;
import vn.easyca.signserver.core.services.signing.signer.CryptoTokenProxy;
import vn.easyca.signserver.core.services.signing.signer.RawSigner;
import vn.easyca.signserver.core.services.signing.signer.PDFSigner;
import vn.easyca.signserver.core.services.signing.signer.XmlSigner;

@Service
public class SigningService {

    private final static String TEM_DIR = "./TemFile/";

    @Autowired
    private CryptoTokenProxyFactory cryptoTokenProxyFactory;

    public PDFSigningDataRes signPDFFile(SigningRequest<PDFSigningContent> request) throws Exception {
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

    public SigningDataResponse<String> signHash(SigningRequest<RawSigningContent> request) throws Exception {
        try {
            TokenInfoDTO tokenInfoDTO = request.getTokenInfoDTO();
            CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(tokenInfoDTO.getSerial(), tokenInfoDTO.getPin());
            return new RawSigner(cryptoTokenProxy).signHash(request);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Can not sign hash");
        }
    }

    public SigningDataResponse<String> signData(SigningRequest<RawSigningContent> request) throws Exception {
        try {
            TokenInfoDTO tokenInfoDTO = request.getTokenInfoDTO();
            CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(tokenInfoDTO.getSerial(), tokenInfoDTO.getPin());
            RawSigner rawSigner = new RawSigner(cryptoTokenProxy);
            return rawSigner.signData(request);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Can not sign data");
        }
    }

    public SigningDataResponse<String> signXML(SigningRequest<XMLSigningContent> request) throws Exception {
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
