package vn.easyca.signserver.webapp.service.signer.rawsigner;

import vn.easyca.signserver.core.sign.integrated.pdf.DigestCreator;
import vn.easyca.signserver.webapp.service.signer.CryptoTokenProxy;

import java.util.Base64;


public class RawSigner {

    private final CryptoTokenProxy cryptoTokenProxy;

    public RawSigner(CryptoTokenProxy cryptoTokenProxy) {
        this.cryptoTokenProxy = cryptoTokenProxy;
    }

    public RawSigningResult signHash(String encodedBase64Data) throws Exception {
        byte[] data = Base64.getDecoder().decode(encodedBase64Data);
        vn.easyca.signserver.core.sign.rawsign.RawSigner rawSigner = new vn.easyca.signserver.core.sign.rawsign.RawSigner();
        data = new DigestCreator().digestWithSHA1Info(data);
        byte[] raw = rawSigner.signHash(data, cryptoTokenProxy.getPrivateKey());
        return new RawSigningResult(cryptoTokenProxy.getBase64Certificate(), Base64.getEncoder().encodeToString(raw));
    }

    public RawSigningResult signData(String encodedBase64Data,String hashAlgorithm) throws Exception {
        if (hashAlgorithm == null)
            hashAlgorithm = "SHA1";
        byte[] data = Base64.getDecoder().decode(encodedBase64Data);
        vn.easyca.signserver.core.sign.rawsign.RawSigner rawSigner = new vn.easyca.signserver.core.sign.rawsign.RawSigner();
        byte[] signature = rawSigner.signData(data,cryptoTokenProxy.getPrivateKey(),hashAlgorithm);
        return new RawSigningResult(cryptoTokenProxy.getBase64Certificate(), Base64.getEncoder().encodeToString(signature));
    }
}



