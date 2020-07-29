package vn.easyca.signserver.sign.core.sign.rawsign;

import javax.crypto.Cipher;
import java.security.PrivateKey;
import java.security.Signature;

/**
 * Created by chen on 7/24/17.
 */
public class RawSigner {

    private final String DEFAULT_SIG_ALGO = "SHA1withRSA";
    private String hashAlgoUsed;

    public byte[] signHashForWindows(byte[] hash, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("NONEwithRSA", "SunMSCAPI");
        signature.initSign(privateKey);
        signature.update(hash);
        return signature.sign();
    }

    public byte[] signHash(byte[] hash, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(hash);
    }

    public byte[] signData(byte[] data, PrivateKey privateKey) throws Exception {
        hashAlgoUsed = DEFAULT_SIG_ALGO;
        Signature signature = Signature.getInstance(DEFAULT_SIG_ALGO);
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    public byte[] signData(byte[] data, PrivateKey privateKey, String hashAlgo) throws Exception {
        Signature signature = null;
        hashAlgo = hashAlgo.trim().toLowerCase();
        hashAlgo = hashAlgo.replace("-", "");
        switch (hashAlgo) {
            case "sha1":
                signature = Signature.getInstance(DEFAULT_SIG_ALGO);
                hashAlgoUsed = DEFAULT_SIG_ALGO;
                break;
            case "sha256":
                signature = Signature.getInstance("SHA256withRSA");
                hashAlgo = "SHA256withRSA";
                break;
            case "sha512":
                signature = Signature.getInstance("SHA512withRSA");
                hashAlgo = "SHA512withRSA";
                break;
            default:
                throw new Exception("Algorithm not supported");
        }
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    public String getHashAlgoUsed() {
        return hashAlgoUsed;
    }
}
