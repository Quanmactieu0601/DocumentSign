package vn.easyca.signserver.core.services.signing.signer;

import vn.easyca.signserver.core.services.signing.dto.request.SigningRequest;
import vn.easyca.signserver.core.services.signing.dto.request.content.RawSigningContent;
import vn.easyca.signserver.core.services.signing.dto.response.SigningDataResponse;
import vn.easyca.signserver.core.utils.CommonUtils;
import vn.easyca.signserver.sign.core.sign.integrated.pdf.DigestCreator;
import java.util.Base64;

public class RawSigner {

    private final CryptoTokenProxy cryptoTokenProxy;

    public RawSigner(CryptoTokenProxy cryptoTokenProxy) {
        this.cryptoTokenProxy = cryptoTokenProxy;
    }

    public SigningDataResponse<String> signHash(SigningRequest<RawSigningContent> request) throws Exception {
        byte[] data = CommonUtils.decodeBase64(request.getContent().getBase64Data());
        vn.easyca.signserver.sign.core.sign.rawsign.RawSigner rawSigner = new vn.easyca.signserver.sign.core.sign.rawsign.RawSigner();
        data = new DigestCreator().hash(data,request.getContent().getHashAlgorithm());
        byte[] raw = rawSigner.signHash(data, cryptoTokenProxy.getPrivateKey());
        return new SigningDataResponse<>(cryptoTokenProxy.getBase64Certificate(), Base64.getEncoder().encodeToString(raw));
    }

    public SigningDataResponse<String> signData(SigningRequest<RawSigningContent> request) throws Exception {
        byte[] data = CommonUtils.decodeBase64(request.getContent().getBase64Data());
        vn.easyca.signserver.sign.core.sign.rawsign.RawSigner rawSigner = new vn.easyca.signserver.sign.core.sign.rawsign.RawSigner();
        data = new DigestCreator().hash(data,request.getContent().getHashAlgorithm());
        byte[] raw = rawSigner.signHash(data, cryptoTokenProxy.getPrivateKey());
        return new SigningDataResponse<>(cryptoTokenProxy.getBase64Certificate(), Base64.getEncoder().encodeToString(raw));
    }
}



