package vn.easyca.signserver.sign.core.sign.rawsign;

import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.X509Certificate;

/**
 * Created by chen on 7/24/17.
 */
public class RawValidator {

    private final String DEFAULT_SIG_ALGO = "SHA1withRSA";

    public boolean verify(byte[] origData, byte[] sig, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance(DEFAULT_SIG_ALGO);
        signature.initVerify(publicKey);
        signature.update(origData);
        return signature.verify(sig);
    }

    public boolean verify(byte[] origData, byte[] sig, X509Certificate cert) throws Exception {
        return verify(origData, sig, cert.getPublicKey());
    }

    public boolean verify(byte[] origData, byte[] sig, PublicKey publicKey, String hashAlgo) throws Exception {
        Signature signature = null;
        hashAlgo = hashAlgo.trim().toLowerCase();
        hashAlgo = hashAlgo.replace("-", "");
        switch (hashAlgo) {
            case "sha1":
                signature = Signature.getInstance("DEFAULT_SIG_ALGO");
                break;
            case "sha256":
                signature = Signature.getInstance("SHA256withRSA");
                break;
            case "sha512":
                signature = Signature.getInstance("SHA512withRSA");
                break;
            default:
                throw new Exception("Algorithm not supported");
        }
        signature.initVerify(publicKey);
        signature.update(origData);
        return signature.verify(sig);
    }

    public boolean verify(byte[] origData, byte[] sig, X509Certificate cert, String hashAlgo) throws Exception {
        return verify(origData, sig, cert.getPublicKey(), hashAlgo);
    }
}
