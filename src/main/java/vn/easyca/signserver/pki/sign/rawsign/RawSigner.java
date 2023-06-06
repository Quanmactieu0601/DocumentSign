package vn.easyca.signserver.pki.sign.rawsign;
import vn.easyca.signserver.pki.sign.commond.DigestCreator;
import javax.crypto.Cipher;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;


public class RawSigner {

    private final String DEFAULT_SIG_ALGO = "SHA1withRSA";

    public byte[] signHashForWindows(byte[] hash, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("NONEwithRSA", "SunMSCAPI");
        signature.initSign(privateKey);
        signature.update(hash);
        return signature.sign();
    }

    // sign hash without digest info (have to add digest info when hash on client-side)
    public byte[] signHashWithoutDigestInfo(byte[] hash, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(hash);
    }

    // sign hash with digest info (no need to add digest info when hash on client-side)
    public byte[] signHashWithDigestInfo(byte[] hash, PrivateKey privateKey, String hashAlgorithm) throws Exception {
        DigestCreator digestCreator = new DigestCreator();
        hash = digestCreator.digestWithSHAInfo(hashAlgorithm, hash);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(hash);
    }

    // TODO: new SAFENETProvider() dang truyen them provider cho Nhi dong, can resolve theo tung crypto token
    public byte[] signData(byte[] data, PrivateKey privateKey, Signature signature) throws Exception {
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

}
