package vn.easyca.signserver.core.services;

import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.domain.CertificateDTO;
import vn.easyca.signserver.core.dto.OptionalDTO;
import vn.easyca.signserver.core.dto.sign.TokenInfoDTO;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningRequest;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningRequestContent;
import vn.easyca.signserver.core.dto.sign.newresponse.SigningResponse;
import vn.easyca.signserver.core.dto.sign.newresponse.SigningResponseContent;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.exception.BadServiceInputAppException;
import vn.easyca.signserver.core.exception.CertificateAppException;
import vn.easyca.signserver.core.exception.CertificateNotFoundAppException;
import vn.easyca.signserver.core.factory.CryptoTokenProxy;
import vn.easyca.signserver.core.factory.CryptoTokenProxyException;
import vn.easyca.signserver.core.factory.CryptoTokenProxyFactory;
import vn.easyca.signserver.pki.sign.integrated.pdf.invisible.InvisiblePDFSigning;
import vn.easyca.signserver.webapp.service.CertificateService;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PDFSigningService {

    private final CryptoTokenProxyFactory cryptoTokenProxyFactory;

    private final CertificateService certificateService;

    private final InvisiblePDFSigning invisiblePDFSigning;

    public PDFSigningService(CertificateService certificateService, CryptoTokenProxyFactory cryptoTokenProxyFactory, InvisiblePDFSigning invisiblePDFSigning) {
        this.cryptoTokenProxyFactory = cryptoTokenProxyFactory;
        this.certificateService = certificateService;
        this.invisiblePDFSigning = invisiblePDFSigning;
    }

    public SigningResponse invisibleSign(SigningRequest request) throws Exception {
        if (request == null)
            throw new BadServiceInputAppException("dont have element to sign", null);
        TokenInfoDTO tokenInfoDTO = request.getTokenInfo();
        if (tokenInfoDTO == null)
            throw new BadServiceInputAppException("tokenInfo object is empty");
        OptionalDTO optionalDTO = request.getOptional();
        String otp = optionalDTO.getOtpCode();
        CertificateDTO certificate = certificateService.getBySerial(tokenInfoDTO.getSerial());
        if (certificate == null)
            throw new CertificateNotFoundAppException();
        if(!certificateService.checkEnoughSigningCountRemain(certificate.getSignedTurnCount(), certificate.getSingingProfile(), request.getSigningRequestContents().size())){
            throw new ApplicationException("Signing count remain is not enough!");
        }
        SigningResponse signingResponse = new SigningResponse();
        CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(certificate, tokenInfoDTO.getPin(), otp);
        String providerName = cryptoTokenProxy.getProviderName();
        PrivateKey privateKey = cryptoTokenProxy.getPrivateKey();
        Certificate x509Certificates = cryptoTokenProxy.getX509Certificate();
        List<SigningResponseContent> responseContentList = new ArrayList<>();
        List<SigningRequestContent> dataList = request.getSigningRequestContents();
        SigningResponseContent responseContent = null;
        String signatureAlgorithm = optionalDTO.getSignatureAlgorithm();
        int numSignatures = 0;
        for (SigningRequestContent data : dataList) {
            byte[] pairResult = invisiblePDFSigning.signPdf(data.getData(), "", "", privateKey, new Certificate[] {x509Certificates}, signatureAlgorithm, providerName);
            responseContent = new SigningResponseContent(data.getDocumentName(), null, pairResult);
            responseContentList.add(responseContent);
            numSignatures++;
        }
        certificateService.updateSignTurn(certificate.getId(), numSignatures);
        signingResponse.setBase64Certificate(cryptoTokenProxy.getBase64Certificate());
        signingResponse.setResponseContentList(responseContentList);
        return signingResponse;
    }
}
