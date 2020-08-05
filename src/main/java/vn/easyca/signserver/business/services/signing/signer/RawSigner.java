package vn.easyca.signserver.business.services.signing.signer;

import vn.easyca.signserver.business.services.signing.dto.request.SigningRequest;
import vn.easyca.signserver.business.services.signing.dto.request.content.RawSigningContent;
import vn.easyca.signserver.business.services.signing.dto.response.SigningDataResponse;
import vn.easyca.signserver.business.utils.CommonUtils;
import vn.easyca.signserver.pki.sign.integrated.pdf.DigestCreator;
import java.util.Base64;

public class RawSigner {

    private final CryptoTokenProxy cryptoTokenProxy;

    public RawSigner(CryptoTokenProxy cryptoTokenProxy) {
        this.cryptoTokenProxy = cryptoTokenProxy;
    }

    public SigningDataResponse<String> signHash(SigningRequest<RawSigningContent> request) throws Exception {
        byte[] data = CommonUtils.decodeBase64(request.getContent().getBase64Data());
        vn.easyca.signserver.pki.sign.rawsign.RawSigner rawSigner = new vn.easyca.signserver.pki.sign.rawsign.RawSigner();
        data = new DigestCreator().hash(data,request.getContent().getHashAlgorithm());
        byte[] raw = rawSigner.signHash(data, cryptoTokenProxy.getPrivateKey());
        return new SigningDataResponse<>(Base64.getEncoder().encodeToString(raw),cryptoTokenProxy.getBase64Certificate());
    }

    public SigningDataResponse<String> signData(SigningRequest<RawSigningContent> request) throws Exception {
        byte[] data = CommonUtils.decodeBase64(request.getContent().getBase64Data());
        vn.easyca.signserver.pki.sign.rawsign.RawSigner rawSigner = new vn.easyca.signserver.pki.sign.rawsign.RawSigner();
        data = new DigestCreator().hash(data,request.getContent().getHashAlgorithm());
        byte[] raw = rawSigner.signHash(data, cryptoTokenProxy.getPrivateKey());
        return new SigningDataResponse<>(Base64.getEncoder().encodeToString(raw),cryptoTokenProxy.getBase64Certificate());
    }
}



