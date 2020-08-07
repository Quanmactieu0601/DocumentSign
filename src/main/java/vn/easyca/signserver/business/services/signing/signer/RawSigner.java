package vn.easyca.signserver.business.services.signing.signer;

import vn.easyca.signserver.business.services.signing.dto.request.SigningRequest;
import vn.easyca.signserver.business.services.signing.dto.request.content.RawBatchSigningContent;
import vn.easyca.signserver.business.services.signing.dto.request.content.RawSigningContent;
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

    public SigningDataResponse<SignResultElement> signHash(SigningRequest<RawSigningContent> request) throws Exception {
        byte[] data = CommonUtils.decodeBase64(request.getContent().getBase64Data());
        vn.easyca.signserver.pki.sign.rawsign.RawSigner rawSigner = new vn.easyca.signserver.pki.sign.rawsign.RawSigner();
        String hashAlgorithm = request.getHashAlgorithm();
        data = new DigestCreator().digestWithSHAInfo(hashAlgorithm, data);
        byte[] raw = rawSigner.signHash(data, cryptoTokenProxy.getPrivateKey());
        SignResultElement signResult = new SignResultElement();
        signResult.setSignature(Base64.getEncoder().encodeToString(raw));
        if (request.isReturnInputData())
            signResult.setInputData(request.getContent().getBase64Data());
        return new SigningDataResponse<>(signResult, cryptoTokenProxy.getBase64Certificate());
    }

    public SigningDataResponse<SignResultElement> signData(SigningRequest<RawSigningContent> request) throws Exception {
        byte[] data = CommonUtils.decodeBase64(request.getContent().getBase64Data());
        vn.easyca.signserver.pki.sign.rawsign.RawSigner rawSigner = new vn.easyca.signserver.pki.sign.rawsign.RawSigner();
        byte[] raw = rawSigner.signData(data, cryptoTokenProxy.getPrivateKey());
        SignResultElement signResult = new SignResultElement();
        signResult.setSignature(Base64.getEncoder().encodeToString(raw));
        if (request.isReturnInputData())
            signResult.setInputData(request.getContent().getBase64Data());
        return new SigningDataResponse<>(signResult, cryptoTokenProxy.getBase64Certificate());
    }

    public SigningDataResponse<HashMap<String, SignResultElement>> signHashBatch(SigningRequest<RawBatchSigningContent> request) throws Exception {
        HashMap<String, SignResultElement> keyAndSignature = new HashMap<>();
        HashMap<String, String> keyAndHash = request.getContent().getBatch();
        vn.easyca.signserver.pki.sign.rawsign.RawSigner rawSigner = new vn.easyca.signserver.pki.sign.rawsign.RawSigner();
        String hashAlgorithm = request.getHashAlgorithm();
        for (Map.Entry me : keyAndHash.entrySet()) {
            byte[] data = CommonUtils.decodeBase64((String) me.getValue());
            data = new DigestCreator().digestWithSHAInfo(hashAlgorithm, data);
            byte[] raw = rawSigner.signHash(data, cryptoTokenProxy.getPrivateKey());
            SignResultElement signResultElement = new SignResultElement();
            signResultElement.setSignature(Base64.getEncoder().encodeToString(raw));
            if (request.getOptional() != null && request.getOptional().isReturnInputData())
                signResultElement.setInputData((String) me.getValue());
            keyAndSignature.put((String) me.getKey(), signResultElement);
        }
        return new SigningDataResponse<>(keyAndSignature, cryptoTokenProxy.getBase64Certificate());
    }

    public SigningDataResponse<HashMap<String, SignResultElement>> signRawBatch(SigningRequest<RawBatchSigningContent> request) throws Exception {
        HashMap<String, SignResultElement> keyAndSignature = new HashMap<>();
        HashMap<String, String> keyAndRaw = request.getContent().getBatch();
        vn.easyca.signserver.pki.sign.rawsign.RawSigner rawSigner = new vn.easyca.signserver.pki.sign.rawsign.RawSigner();
        for (Map.Entry me : keyAndRaw.entrySet()) {
            byte[] data = CommonUtils.decodeBase64((String) me.getValue());
            byte[] raw = rawSigner.signData(data, cryptoTokenProxy.getPrivateKey());
            SignResultElement signResultElement = new SignResultElement();
            signResultElement.setSignature(Base64.getEncoder().encodeToString(raw));
            if (request.getOptional() != null && request.getOptional().isReturnInputData())
                signResultElement.setInputData((String) me.getValue());
            keyAndSignature.put((String) me.getKey(), signResultElement);
        }
        return new SigningDataResponse<>(keyAndSignature, cryptoTokenProxy.getBase64Certificate());
    }


}



