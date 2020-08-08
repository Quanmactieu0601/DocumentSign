package vn.easyca.signserver.business.services.signing.signer;

import vn.easyca.signserver.business.services.signing.dto.request.SignElement;
import vn.easyca.signserver.business.services.signing.dto.request.SignRequest;
import vn.easyca.signserver.business.services.signing.dto.request.content.RawSignContent;
import vn.easyca.signserver.business.services.signing.dto.response.SignResultElement;
import vn.easyca.signserver.business.services.signing.dto.response.SigningDataResponse;
import vn.easyca.signserver.business.utils.CommonUtils;
import vn.easyca.signserver.pki.sign.integrated.pdf.DigestCreator;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class RawSigner {

    private final CryptoTokenProxy cryptoTokenProxy;

    public RawSigner(CryptoTokenProxy cryptoTokenProxy) {
        this.cryptoTokenProxy = cryptoTokenProxy;
    }

    public SigningDataResponse<Map<String, SignResultElement>> signHash(SignRequest<String> request) throws Exception {
        Map<String, SignResultElement> keyAndSignature = new HashMap<>();
        Map<String, SignElement<String>> keyAndHash = request.getSignElements();
        vn.easyca.signserver.pki.sign.rawsign.RawSigner rawSigner = new vn.easyca.signserver.pki.sign.rawsign.RawSigner();
        String hashAlgorithm = request.getHashAlgorithm();
        for (Map.Entry<String, SignElement<String>> me : keyAndHash.entrySet()) {
            byte[] data = CommonUtils.decodeBase64(me.getValue().getContent());
            data = new DigestCreator().digestWithSHAInfo(hashAlgorithm, data);
            byte[] raw = rawSigner.signHash(data, cryptoTokenProxy.getPrivateKey());
            SignResultElement signResultElement = new SignResultElement();
            signResultElement.setSignature(Base64.getEncoder().encodeToString(raw));
            if (request.getOptional() != null && request.getOptional().isReturnInputData())
                signResultElement.setInputData(me.getValue().getContent());
            keyAndSignature.put((String) me.getKey(), signResultElement);
        }
        return new SigningDataResponse<>(keyAndSignature, cryptoTokenProxy.getBase64Certificate());
    }

    public SigningDataResponse<Map<String, SignResultElement>> signRaw(SignRequest<String> request) throws Exception {
        Map<String, SignResultElement> keyAndSignature = new HashMap<>();
        Map<String, SignElement<String>> keyAndHash = request.getSignElements();
        vn.easyca.signserver.pki.sign.rawsign.RawSigner rawSigner = new vn.easyca.signserver.pki.sign.rawsign.RawSigner();
        for (Map.Entry<String, SignElement<String>> me : keyAndHash.entrySet()) {
            byte[] data = CommonUtils.decodeBase64(me.getValue().getContent());
            byte[] raw = rawSigner.signHash(data, cryptoTokenProxy.getPrivateKey());
            SignResultElement signResultElement = new SignResultElement();
            signResultElement.setSignature(Base64.getEncoder().encodeToString(raw));
            if (request.getOptional() != null && request.getOptional().isReturnInputData())
                signResultElement.setInputData(me.getValue().getContent());
            keyAndSignature.put(me.getKey(), signResultElement);
        }
        return new SigningDataResponse<>(keyAndSignature, cryptoTokenProxy.getBase64Certificate());
    }
}



