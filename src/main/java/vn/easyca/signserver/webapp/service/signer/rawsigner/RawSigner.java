package vn.easyca.signserver.webapp.service.signer.rawsigner;

import vn.easyca.signserver.core.sign.integrated.pdf.DigestCreator;
import vn.easyca.signserver.webapp.service.signer.CryptoTokenProxy;

import java.util.Base64;


public class RawSigner {

    private CryptoTokenProxy cryptoTokenProxy;

    public RawSigner(CryptoTokenProxy cryptoTokenProxy) {
        this.cryptoTokenProxy = cryptoTokenProxy;
    }

    public RawSigningResult signData(byte[] signData) throws Exception {
        vn.easyca.signserver.core.sign.rawsign.RawSigner rawSigner = new vn.easyca.signserver.core.sign.rawsign.RawSigner();
        signData = new DigestCreator().digestWithSHA1Info(signData);
        byte[] raw = rawSigner.signHash(signData, cryptoTokenProxy.getPrivateKey());
        return new RawSigningResult(cryptoTokenProxy.getBase64Certificate(), Base64.getEncoder().encodeToString(raw));
    }
}



