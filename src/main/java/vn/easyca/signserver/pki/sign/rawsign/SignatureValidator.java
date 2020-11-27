package vn.easyca.signserver.pki.sign.rawsign;

import au.com.safenet.crypto.provider.SAFENETProvider;
import vn.easyca.signserver.pki.sign.commond.DigestCreator;
import vn.easyca.signserver.webapp.config.Constants;

import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.X509Certificate;

/**
 * Created by chen on 7/24/17.
 */
public class SignatureValidator {

    private final String DEFAULT_SIG_ALGO = "SHA1withRSA";

    // TODO: new SAFENETProvider() dang truyen them provider cho Nhi dong, can resolve theo tung crypto token
    public boolean verify(byte[] origData, byte[] sig, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance("NONEwithRSA", new SAFENETProvider());
        signature.initVerify(publicKey);
        signature.update(origData);
        return signature.verify(sig);
    }

    public boolean verify(byte[] origData, byte[] sig, X509Certificate cert) throws Exception {
        return verify(origData, sig, cert.getPublicKey());
    }

    // TODO: new SAFENETProvider() dang truyen them provider cho Nhi dong, can resolve theo tung crypto token
    public boolean verify(byte[] origData, byte[] sig, PublicKey publicKey, String hashAlgo) throws Exception {
        Signature signature = null;
        hashAlgo = hashAlgo.trim().toLowerCase();
        hashAlgo = hashAlgo.replace("-", "");
        switch (hashAlgo) {
            case Constants.HASH_ALGORITHM.SHA1:
                signature = Signature.getInstance(DEFAULT_SIG_ALGO);
                break;
            case Constants.HASH_ALGORITHM.SHA256:
                signature = Signature.getInstance("SHA256withRSA");
                break;
            case Constants.HASH_ALGORITHM.SHA512:
                signature = Signature.getInstance("SHA512withRSA");
                break;
            default:
                throw new Exception("Algorithm not supported");
        };
        signature.initVerify(publicKey);
        signature.update(origData);
        return signature.verify(sig);
    }

    public boolean verify(byte[] origData, byte[] sig, X509Certificate cert, String hashAlgo) throws Exception {
        return verify(origData, sig, cert.getPublicKey(), hashAlgo);
    }

}
