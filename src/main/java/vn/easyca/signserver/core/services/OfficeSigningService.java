package vn.easyca.signserver.core.services;

import javafx.util.Pair;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.dto.sign.TokenInfoDTO;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningRequestContent;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningRequest;
import vn.easyca.signserver.core.dto.sign.newresponse.SigningResponse;
import vn.easyca.signserver.core.dto.sign.newresponse.SigningResponseContent;
import vn.easyca.signserver.core.exception.*;
import vn.easyca.signserver.core.model.CryptoTokenProxy;
import vn.easyca.signserver.core.model.CryptoTokenProxyException;
import vn.easyca.signserver.core.model.CryptoTokenProxyFactory;
import vn.easyca.signserver.core.repository.CertificateRepository;
import vn.easyca.signserver.pki.sign.integrated.office.OfficeSigner;

import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class OfficeSigningService {

    private final CryptoTokenProxyFactory cryptoTokenProxyFactory;

    private final CertificateRepository certificateRepository;

    public OfficeSigningService(CertificateRepository certificateRepository) {
        this.cryptoTokenProxyFactory = new CryptoTokenProxyFactory();
        this.certificateRepository = certificateRepository;
    }

    public SigningResponse sign(SigningRequest request) throws Exception {
        if (request == null)
            throw new BadServiceInputAppException("dont have element to sign", null);
        TokenInfoDTO tokenInfoDTO = request.getTokenInfo();
        vn.easyca.signserver.core.domain.Certificate certificate = certificateRepository.getBySerial(tokenInfoDTO.getSerial());
        if (certificate == null)
            throw new CertificateNotFoundAppException();
        CryptoTokenProxy cryptoTokenProxy = null;
        SigningResponse signingResponse = new SigningResponse();
        try {
            cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(certificate, request.getTokenInfo().getPin());
            PrivateKey privateKey = cryptoTokenProxy.getPrivateKey();
            OfficeSigner officeSigner = new OfficeSigner();
            List<X509Certificate> x509Certificates = Arrays.asList(cryptoTokenProxy.getX509Certificate());
            List<SigningResponseContent> responseContentList = new ArrayList<>();
            List<SigningRequestContent> dataList = request.getSigningRequestContents();
            SigningResponseContent responseContent = null;
            for (SigningRequestContent data : dataList) {
                //TODO: pass hashAlgorithm
                Pair<byte[], byte[]> pairResult = officeSigner.signOOXMLFile(data.getData(), privateKey, x509Certificates, null);
                responseContent = new SigningResponseContent(data.getDocumentName(), pairResult.getKey(), pairResult.getValue());
                responseContentList.add(responseContent);
            }
            signingResponse.setBase64Certificate(cryptoTokenProxy.getBase64Certificate());
            signingResponse.setResponseContentList(responseContentList);
            return signingResponse;
        } catch (CryptoTokenProxyException | CertificateException e) {
            throw new CertificateAppException("Certificate has error", e);
        }
    }
}
