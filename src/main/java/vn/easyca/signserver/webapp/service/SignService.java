package vn.easyca.signserver.webapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.webapp.service.dto.*;
import vn.easyca.signserver.webapp.service.dto.request.SignPDFRequestDto;
import vn.easyca.signserver.webapp.service.dto.request.SignDataRequest;
import vn.easyca.signserver.webapp.service.dto.request.SignXMLRequest;
import vn.easyca.signserver.webapp.service.dto.response.PDFSignResponse;
import vn.easyca.signserver.webapp.service.dto.response.SignDataResponse;
import vn.easyca.signserver.webapp.service.signer.CryptoTokenProxyFactory;
import vn.easyca.signserver.webapp.service.signer.CryptoTokenProxy;
import vn.easyca.signserver.webapp.service.signer.rawsigner.RawSigningResult;
import vn.easyca.signserver.webapp.service.signer.rawsigner.RawSigner;
import vn.easyca.signserver.webapp.service.signer.pdfsigner.PDFSigner;
import vn.easyca.signserver.webapp.service.signer.xmlsigner.XmlSigner;

@Service
public class SignService {

    private final String temDir = "./TemFile/";
    @Autowired
    private CryptoTokenProxyFactory cryptoTokenProxyFactory;

    public PDFSignResponse signPDFFile(SignPDFRequestDto request) throws Exception {
        try {
            CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(request.getTokenInfo().getSerial(), request.getTokenInfo().getPin());
            PDFSigner pdfSigner = new PDFSigner(cryptoTokenProxy, temDir);
            byte[] signedContent = pdfSigner.signPDF(request);
            return new PDFSignResponse(signedContent);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Can not sign pdf file");
        }
    }

    public SignDataResponse signHash(SignDataRequest request) throws Exception {
        try {
            CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(request.getTokenInfo().getSerial(), request.getTokenInfo().getPin());
            RawSigningResult result = new RawSigner(cryptoTokenProxy).signHash(request.getBase64Data());
            return new SignDataResponse(result.getSignatureValue(), result.getCertificate());
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Can not sign hash");
        }
    }


    public SignDataResponse signData(SignDataRequest request) throws Exception {
        try {
            CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(request.getTokenInfo().getSerial(), request.getTokenInfo().getPin());
            RawSigner rawSigner = new RawSigner(cryptoTokenProxy);
            RawSigningResult result = rawSigner.signData(request.getBase64Data(),request.getOptional().getHashAlgorithm());
            return new SignDataResponse(result.getSignatureValue(), result.getCertificate());
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Can not sign hash");
        }
    }

    public String signXML(SignXMLRequest request) throws Exception {
        try {
            CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(request.getTokenInfo().getSerial(), request.getTokenInfo().getPin());
            XmlSigner xmlSigner = new XmlSigner(cryptoTokenProxy);
            return xmlSigner.sign(request);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Can not sign Xml");
        }
    }
}
