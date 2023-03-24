package vn.easyca.signserver.core.services;

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
import vn.easyca.signserver.core.factory.CryptoTokenProxyFactory;
import vn.easyca.signserver.pki.sign.integrated.xml.SignXMLLib;
import vn.easyca.signserver.webapp.service.CertificateService;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

@Service
public class XMLSigningService {

    private final CryptoTokenProxyFactory cryptoTokenProxyFactory;

    private final CertificateService certificateService;

    public XMLSigningService(CertificateService certificateService, CryptoTokenProxyFactory cryptoTokenProxyFactory) {
        this.cryptoTokenProxyFactory = cryptoTokenProxyFactory;
        this.certificateService = certificateService;
    }

    public SigningResponse sign(SigningRequest request) throws ApplicationException {
        TokenInfoDTO tokenInfoDTO = request.getTokenInfo();
        if (tokenInfoDTO == null)
            throw new BadServiceInputAppException("tokenInfo object is empty");
        CertificateDTO certificateDTO = certificateService.getBySerial(tokenInfoDTO.getSerial());
        if (certificateDTO == null)
            throw new CertificateNotFoundAppException();

        if(!certificateService.checkEnoughSigningCountRemain(certificateDTO.getSignedTurnCount(), certificateDTO.getSingingProfile(), request.getSigningRequestContents().size())){
            throw new ApplicationException("Signing count remain is not enough!");
        }

        OptionalDTO optionalDTO = request.getOptional();
        String otp = optionalDTO.getOtpCode();

        CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(certificateDTO, tokenInfoDTO.getPin(), otp);
        SignXMLLib lib = new SignXMLLib();
        SigningResponse signingResponse = new SigningResponse();
        try {
            PrivateKey privateKey = cryptoTokenProxy.getPrivateKey();
            X509Certificate x509Certificate = cryptoTokenProxy.getX509Certificate();
            List<SigningResponseContent> responseContentList = new ArrayList<>();
            List<SigningRequestContent> dataList = request.getSigningRequestContents();
            SigningResponseContent responseContent = null;
            int numSignatures = 0;
            for (SigningRequestContent data : dataList) {
                String result = lib.generateXMLDigitalSignature(new String(data.getData()), privateKey, x509Certificate, cryptoTokenProxy.getProviderName());
                responseContent = new SigningResponseContent(data.getDocumentName(), null, result.getBytes());
                responseContentList.add(responseContent);
                numSignatures++;
            }
            certificateService.updateSignTurn(certificateDTO.getId(), numSignatures);
            signingResponse.setBase64Certificate(cryptoTokenProxy.getBase64Certificate());
            signingResponse.setResponseContentList(responseContentList);
            return signingResponse;
        } catch (Exception ex) {
            throw new SigningAppException("Sign Xml hash error", ex);
        }
    }
}
