package core.services;

import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.util.Pair;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.domain.CertificateDTO;
import vn.easyca.signserver.core.dto.OptionalDTO;
import vn.easyca.signserver.core.dto.sign.TokenInfoDTO;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningRequest;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningRequestContent;
import vn.easyca.signserver.core.dto.sign.newresponse.SigningResponse;
import vn.easyca.signserver.core.dto.sign.newresponse.SigningResponseContent;
import vn.easyca.signserver.core.exception.*;
import vn.easyca.signserver.core.factory.CryptoTokenProxy;
import vn.easyca.signserver.core.factory.CryptoTokenProxyException;
import vn.easyca.signserver.core.factory.CryptoTokenProxyFactory;
import vn.easyca.signserver.pki.sign.integrated.office.OfficeSigner;
import vn.easyca.signserver.webapp.service.CertificateService;

@Service
public class OfficeSigningService {

    private final CryptoTokenProxyFactory cryptoTokenProxyFactory;

    private final CertificateService certificateService;

    public OfficeSigningService(CertificateService certificateService, CryptoTokenProxyFactory cryptoTokenProxyFactory) {
        this.cryptoTokenProxyFactory = cryptoTokenProxyFactory;
        this.certificateService = certificateService;
    }

    public SigningResponse sign(SigningRequest request) throws Exception {
        if (request == null) throw new BadServiceInputAppException("request object is empty", null);
        TokenInfoDTO tokenInfoDTO = request.getTokenInfo();
        if (tokenInfoDTO == null) throw new BadServiceInputAppException("tokenInfo object is empty");

        CertificateDTO certificate = certificateService.getBySerial(tokenInfoDTO.getSerial());
        if (certificate == null) throw new CertificateNotFoundAppException();
        if (
            !certificateService.checkEnoughSigningCountRemain(
                certificate.getSignedTurnCount(),
                certificate.getSingingProfile(),
                request.getSigningRequestContents().size()
            )
        ) {
            throw new ApplicationException("Signing count remain is not enough!");
        }

        OptionalDTO optionalDTO = request.getOptional();
        String otp = optionalDTO.getOtpCode();

        SigningResponse signingResponse = new SigningResponse();
        try {
            CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(certificate, tokenInfoDTO.getPin(), otp);
            PrivateKey privateKey = cryptoTokenProxy.getPrivateKey();
            OfficeSigner officeSigner = new OfficeSigner();
            List<X509Certificate> x509Certificates = Arrays.asList(cryptoTokenProxy.getX509Certificate());
            List<SigningResponseContent> responseContentList = new ArrayList<>();
            List<SigningRequestContent> dataList = request.getSigningRequestContents();
            SigningResponseContent responseContent = null;
            Signature signatureInstance = cryptoTokenProxy.getCryptoToken().getSignatureInstance(optionalDTO.getHashAlgorithm());
            int numSignatures = 0;
            for (SigningRequestContent data : dataList) {
                Pair<byte[], byte[]> pairResult = officeSigner.signOOXMLFile(
                    data.getData(),
                    privateKey,
                    x509Certificates,
                    signatureInstance
                );
                responseContent = new SigningResponseContent(data.getDocumentName(), pairResult.getKey(), pairResult.getValue());
                responseContentList.add(responseContent);
                numSignatures++;
            }
            certificateService.updateSignTurn(certificate.getSerial(), numSignatures);
            signingResponse.setBase64Certificate(cryptoTokenProxy.getBase64Certificate());
            signingResponse.setResponseContentList(responseContentList);
            return signingResponse;
        } catch (CryptoTokenProxyException | CertificateException e) {
            throw new CertificateAppException("Certificate has error", e);
        }
    }
}
