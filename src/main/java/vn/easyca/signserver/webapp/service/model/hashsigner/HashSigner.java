package vn.easyca.signserver.webapp.service.model.hashsigner;

import vn.easyca.signserver.core.sign.rawsign.RawSigner;
import vn.easyca.signserver.webapp.service.model.Signature;

import java.util.Base64;


public class HashSigner {

    private Signature signature;

    public HashSigner(Signature signature) {
        this.signature = signature;
    }
    public HashSignResult signHash(byte[] hashRaw) throws Exception {

        RawSigner rawSigner = new RawSigner();
        byte[] raw = rawSigner.signHash(hashRaw,signature.getPrivateKey());
        return new HashSignResult(signature.getBase64Certificate(), Base64.getEncoder().encodeToString(raw));
    }
}



