package vn.easyca.signserver.webapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.cryptotoken.CryptoToken;
import vn.easyca.signserver.webapp.service.dto.*;
import vn.easyca.signserver.webapp.service.dto.request.SignPDFRequest;
import vn.easyca.signserver.webapp.service.dto.request.SignHashRequest;
import vn.easyca.signserver.webapp.service.dto.request.SignXMLRequest;
import vn.easyca.signserver.webapp.service.dto.response.PDFSignResponse;
import vn.easyca.signserver.webapp.service.dto.response.SignHashResponse;
import vn.easyca.signserver.webapp.service.error.sign.InitTokenProxyException;
import vn.easyca.signserver.webapp.service.error.sign.PDFSignException;
import vn.easyca.signserver.webapp.service.error.sign.XmlSignException;
import vn.easyca.signserver.webapp.service.model.CryptoTokenFactory;
import vn.easyca.signserver.webapp.service.model.CryptoTokenProxy;
import vn.easyca.signserver.webapp.service.model.rawsigner.RawSigningResult;
import vn.easyca.signserver.webapp.service.model.rawsigner.RawSigner;
import vn.easyca.signserver.webapp.service.model.pdfsigner.PDFSigner;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.repository.CertificateRepository;
import vn.easyca.signserver.webapp.service.model.xmlsigner.XmlSigner;

@Service
public class SignService {
    private final Logger log = LoggerFactory.getLogger(SignService.class);

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private Encryption encryption;

    private final String temDir = "./TemFile/";

    public PDFSignResponse signPDFFile(SignPDFRequest request) throws InitTokenProxyException, PDFSignException {
        CryptoTokenProxy cryptoTokenProxy = getCryptoTokenProxy(request.getTokenInfoDto());
        PDFSigner pdfSigner = new PDFSigner(cryptoTokenProxy, temDir);
        try {
            byte[] signedContent = pdfSigner.signPDF(request);
            return new PDFSignResponse(signedContent);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new PDFSignException();
        }
    }

    public SignHashResponse signData(SignHashRequest request) throws XmlSignException, InitTokenProxyException {
        CryptoTokenProxy cryptoTokenProxy = getCryptoTokenProxy(request.getTokenInfoDto());
        try {
            RawSigningResult result = new RawSigner(cryptoTokenProxy).signData(request.getBytes());
            return new SignHashResponse(result.getSignatureValue(), result.getCertificate());
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new XmlSignException();
        }
    }

    public String signXML(SignXMLRequest request, TokenInfoDto tokenInfoDto) throws InitTokenProxyException, XmlSignException {
        CryptoTokenProxy cryptoTokenProxy = getCryptoTokenProxy(tokenInfoDto);
        XmlSigner xmlSigner = new XmlSigner(cryptoTokenProxy);
        try {
            return xmlSigner.sign(request);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new XmlSignException();
        }
    }

    private CryptoTokenProxy getCryptoTokenProxy(TokenInfoDto tokenInfoDto) throws InitTokenProxyException {
        Certificate certificate = certificateRepository.getCertificateBySerial(tokenInfoDto.getSerial());
        if (certificate == null)
            throw new InitTokenProxyException(String.format("Chứng thư số có serial %s không tồn tại trong hệ ", tokenInfoDto.getSerial()));
        try {
            CryptoTokenFactory cryptoTokenFactory = new CryptoTokenFactory();
            CryptoToken cryptoToken = cryptoTokenFactory.resolveToken(certificate, tokenInfoDto.getPin());
            CryptoTokenProxy cryptoTokenProxy = new CryptoTokenProxy(cryptoToken, certificate);
            return cryptoTokenProxy;
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new InitTokenProxyException(String.format("Chứng thư số có serial %s không tạo được CryptoTokenProxy ", tokenInfoDto.getSerial()));
        }
    }
}
