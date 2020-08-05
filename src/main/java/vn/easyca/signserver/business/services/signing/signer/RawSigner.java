package vn.easyca.signserver.business.services.signing.signer;

import vn.easyca.signserver.business.services.signing.dto.request.SigningRequest;
import vn.easyca.signserver.business.services.signing.dto.request.content.BatchRawSigningContent;
import vn.easyca.signserver.business.services.signing.dto.request.content.RawSigningContent;
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

    public SigningDataResponse<String> signHash(SigningRequest<RawSigningContent> request) throws Exception {
        byte[] data = CommonUtils.decodeBase64(request.getContent().getBase64Data());
        vn.easyca.signserver.pki.sign.rawsign.RawSigner rawSigner = new vn.easyca.signserver.pki.sign.rawsign.RawSigner();
        String hashAlgo = request.getHashAlgorithm();
        data = new DigestCreator().hash(data, hashAlgo);
        byte[] raw = rawSigner.signHash(data, cryptoTokenProxy.getPrivateKey());
        return new SigningDataResponse<>(Base64.getEncoder().encodeToString(raw), cryptoTokenProxy.getBase64Certificate());
    }

    public SigningDataResponse<HashMap<String, String>> signBatchHash(SigningRequest<BatchRawSigningContent> request) throws Exception {
        HashMap<String, String> keyAndSignature = new HashMap<>();
        HashMap<String, String> keyAndHash = request.getContent().getBatch();
        vn.easyca.signserver.pki.sign.rawsign.RawSigner rawSigner = new vn.easyca.signserver.pki.sign.rawsign.RawSigner();
        String hashAlgorithm = request.getHashAlgorithm();
        for (Map.Entry me : keyAndHash.entrySet()) {
            byte[] data = CommonUtils.decodeBase64((String) me.getValue());
            data = new DigestCreator().hash(data, hashAlgorithm);
            byte[] raw = rawSigner.signHash(data, cryptoTokenProxy.getPrivateKey());
            keyAndSignature.put((String) me.getKey(), Base64.getEncoder().encodeToString(raw));
        }
        return new SigningDataResponse<HashMap<String, String>>(keyAndSignature, cryptoTokenProxy.getBase64Certificate());
    }


    public SigningDataResponse<String> signData(SigningRequest<RawSigningContent> request) throws Exception {
        byte[] data = CommonUtils.decodeBase64(request.getContent().getBase64Data());
        vn.easyca.signserver.pki.sign.rawsign.RawSigner rawSigner = new vn.easyca.signserver.pki.sign.rawsign.RawSigner();
        String hashAlgo = request.getHashAlgorithm();
        data = new DigestCreator().hash(data, hashAlgo);
        byte[] raw = rawSigner.signHash(data, cryptoTokenProxy.getPrivateKey());
        return new SigningDataResponse<>(Base64.getEncoder().encodeToString(raw), cryptoTokenProxy.getBase64Certificate());
    }
}



