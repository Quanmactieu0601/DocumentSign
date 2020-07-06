package vn.easyca.signserver.webapp.service.model.hashsigner;

import vn.easyca.signserver.core.sign.rawsign.RawSigner;
import vn.easyca.signserver.webapp.service.model.CryptoTokenProxy;

import java.util.Base64;


public class HashSigner {

    private CryptoTokenProxy cryptoTokenProxy;

    public HashSigner(CryptoTokenProxy cryptoTokenProxy) {
        this.cryptoTokenProxy = cryptoTokenProxy;
    }
    public HashSignResult signHash(byte[] hashRaw) throws Exception {

        RawSigner rawSigner = new RawSigner();
        byte[] raw = rawSigner.signHash(hashRaw, cryptoTokenProxy.getPrivateKey());
        return new HashSignResult(cryptoTokenProxy.getBase64Certificate(), Base64.getEncoder().encodeToString(raw));
    }
}



