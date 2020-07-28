package vn.easyca.signserver.webapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.webapp.service.dto.signing.TokenInfoDTO;
import vn.easyca.signserver.webapp.service.dto.signing.request.SigningRequest;
import vn.easyca.signserver.webapp.service.dto.signing.request.PDFSigningData;
import vn.easyca.signserver.webapp.service.dto.signing.request.RawSigningData;
import vn.easyca.signserver.webapp.service.dto.signing.request.XMLSigningData;
import vn.easyca.signserver.webapp.service.dto.signing.response.PDFSigningDataRes;
import vn.easyca.signserver.webapp.service.dto.signing.response.SigningDataResponse;
import vn.easyca.signserver.webapp.service.signer.CryptoTokenProxyFactory;
import vn.easyca.signserver.webapp.service.signer.CryptoTokenProxy;
import vn.easyca.signserver.webapp.service.signer.rawsigner.RawSigningResult;
import vn.easyca.signserver.webapp.service.signer.rawsigner.RawSigner;
import vn.easyca.signserver.webapp.service.signer.pdfsigner.PDFSigner;
import vn.easyca.signserver.webapp.service.signer.xmlsigner.XmlSigner;

@Service
public class SigningService {

    private final static String TEM_DIR = "./TemFile/";

    @Autowired
    private CryptoTokenProxyFactory cryptoTokenProxyFactory;

    public PDFSigningDataRes signPDFFile(SigningRequest<PDFSigningData> request) throws Exception {
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

    public SigningDataResponse signHash(SigningRequest<RawSigningData> request) throws Exception {
        try {
            TokenInfoDTO tokenInfoDTO = request.getTokenInfoDTO();
            CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(tokenInfoDTO.getSerial(), tokenInfoDTO.getPin());
            RawSigningResult result = new RawSigner(cryptoTokenProxy).signHash(request.getData().getBase64Data());
            return new SigningDataResponse(result.getSignatureValue(), result.getCertificate());
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Can not sign hash");
        }
    }

    public SigningDataResponse signData(SigningRequest<RawSigningData> request) throws Exception {
        try {
            TokenInfoDTO tokenInfoDTO = request.getTokenInfoDTO();
            CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(tokenInfoDTO.getSerial(), tokenInfoDTO.getPin());
            RawSigner rawSigner = new RawSigner(cryptoTokenProxy);
            RawSigningResult result = rawSigner.signData(request.getData().getBase64Data(), request.getOptional().getHashAlgorithm());
            return new SigningDataResponse(result.getSignatureValue(), result.getCertificate());
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Can not sign data");
        }
    }

    public String signXML(SigningRequest<XMLSigningData> request) throws Exception {
        try {
            TokenInfoDTO tokenInfoDTO = request.getTokenInfoDTO();
            CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(tokenInfoDTO.getSerial(), tokenInfoDTO.getPin());
            XmlSigner xmlSigner = new XmlSigner(cryptoTokenProxy);
            return xmlSigner.sign(request);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Can not sign Xml");
        }
    }
}
