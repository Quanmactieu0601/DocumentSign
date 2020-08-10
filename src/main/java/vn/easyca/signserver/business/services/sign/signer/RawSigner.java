package vn.easyca.signserver.business.services.sign.signer;

import vn.easyca.signserver.business.services.sign.dto.request.SignElement;
import vn.easyca.signserver.business.services.sign.dto.request.SignRequest;
import vn.easyca.signserver.business.services.sign.dto.response.SignResultElement;
import vn.easyca.signserver.business.services.sign.dto.response.SignDataResponse;
import vn.easyca.signserver.business.utils.CommonUtils;

import java.util.*;

public class RawSigner {

    private final CryptoTokenProxy cryptoTokenProxy;

    public RawSigner(CryptoTokenProxy cryptoTokenProxy) {
        this.cryptoTokenProxy = cryptoTokenProxy;
    }

    public SignDataResponse<List<SignResultElement>> signHash(SignRequest<String> request) throws Exception {
        List<SignResultElement> resultElements = new ArrayList<>();
        List<SignElement<String>> signElements = request.getSignElements();
        vn.easyca.signserver.pki.sign.rawsign.RawSigner rawSigner = new vn.easyca.signserver.pki.sign.rawsign.RawSigner();
        for (SignElement<String> signElement : signElements) {
            byte[] hash = CommonUtils.decodeBase64(signElement.getContent());
            byte[] signature = rawSigner.signHash(hash, cryptoTokenProxy.getPrivateKey());
            String signContent = request.isReturnInputData() ? signElement.getContent() : null;
            SignResultElement signResultElement = SignResultElement.create(signature,signContent,signElement.getKey());
            resultElements.add(signResultElement);
        }
        return new SignDataResponse<>(resultElements, cryptoTokenProxy.getBase64Certificate());
    }

    public SignDataResponse<List<SignResultElement>> signRaw(SignRequest<String> request) throws Exception {
        List<SignResultElement> resultElements = new ArrayList<>();
        List<SignElement<String>> signElements = request.getSignElements();
        vn.easyca.signserver.pki.sign.rawsign.RawSigner rawSigner = new vn.easyca.signserver.pki.sign.rawsign.RawSigner();
        for (SignElement<String> signElement : signElements) {
            byte[] data = CommonUtils.decodeBase64(signElement.getContent());
            byte[] signature = rawSigner.signData(data, cryptoTokenProxy.getPrivateKey(),request.getHashAlgorithm());
            String signContent = request.isReturnInputData() ? signElement.getContent() : null;
            SignResultElement signResultElement = SignResultElement.create(signature,signContent,signElement.getKey());
            resultElements.add(signResultElement);
        }
        return new SignDataResponse<>(resultElements, cryptoTokenProxy.getBase64Certificate());
    }
}



