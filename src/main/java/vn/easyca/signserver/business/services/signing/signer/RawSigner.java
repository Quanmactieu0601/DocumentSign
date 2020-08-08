package vn.easyca.signserver.business.services.signing.signer;

import vn.easyca.signserver.business.services.signing.dto.request.SignElement;
import vn.easyca.signserver.business.services.signing.dto.request.SignRequest;
import vn.easyca.signserver.business.services.signing.dto.request.content.RawSignContent;
import vn.easyca.signserver.business.services.signing.dto.response.SignResultElement;
import vn.easyca.signserver.business.services.signing.dto.response.SigningDataResponse;
import vn.easyca.signserver.business.utils.CommonUtils;
import vn.easyca.signserver.pki.sign.integrated.pdf.DigestCreator;

import java.util.*;

public class RawSigner {

    private final CryptoTokenProxy cryptoTokenProxy;

    public RawSigner(CryptoTokenProxy cryptoTokenProxy) {
        this.cryptoTokenProxy = cryptoTokenProxy;
    }

    public SigningDataResponse<List<SignResultElement>> signHash(SignRequest<String> request) throws Exception {

        List<SignResultElement> resultElements = new ArrayList<>();
        List<SignElement<String>> signElements = request.getSignElements();
        vn.easyca.signserver.pki.sign.rawsign.RawSigner rawSigner = new vn.easyca.signserver.pki.sign.rawsign.RawSigner();
        String hashAlgorithm = request.getHashAlgorithm();
        for (SignElement<String> signElement : signElements) {
            byte[] data = CommonUtils.decodeBase64(signElement.getContent());
            data = new DigestCreator().digestWithSHAInfo(hashAlgorithm, data);
            byte[] raw = rawSigner.signHash(data, cryptoTokenProxy.getPrivateKey());
            SignResultElement signResultElement = new SignResultElement();
            signResultElement.setSignature(Base64.getEncoder().encodeToString(raw));
            if (request.getOptional() != null && request.getOptional().isReturnInputData())
                signResultElement.setInputData(signElement.getContent());
            signResultElement.setKey(signElement.getKey());
            resultElements.add(signResultElement);
        }
        return new SigningDataResponse<>(resultElements, cryptoTokenProxy.getBase64Certificate());
    }

    public SigningDataResponse<List<SignResultElement>> signRaw(SignRequest<String> request) throws Exception {
        List<SignResultElement> resultElements = new ArrayList<>();
        List<SignElement<String>> signElements = request.getSignElements();
        vn.easyca.signserver.pki.sign.rawsign.RawSigner rawSigner = new vn.easyca.signserver.pki.sign.rawsign.RawSigner();
        for (SignElement<String> signElement : signElements) {
            byte[] data = CommonUtils.decodeBase64(signElement.getContent());
            byte[] raw = rawSigner.signHash(data, cryptoTokenProxy.getPrivateKey());
            SignResultElement signResultElement = new SignResultElement();
            signResultElement.setSignature(Base64.getEncoder().encodeToString(raw));
            if (request.getOptional() != null && request.getOptional().isReturnInputData())
                signResultElement.setInputData(signElement.getContent());
            signResultElement.setKey(signElement.getKey());
            resultElements.add(signResultElement);
        }
        return new SigningDataResponse<>(resultElements, cryptoTokenProxy.getBase64Certificate());
    }
}



