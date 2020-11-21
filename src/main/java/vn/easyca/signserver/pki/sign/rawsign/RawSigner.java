package vn.easyca.signserver.pki.sign.rawsign;
import au.com.safenet.crypto.provider.slot0.SAFENETProvider;
import vn.easyca.signserver.pki.sign.commond.DigestCreator;
import javax.crypto.Cipher;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;


public class RawSigner {

    private final String DEFAULT_SIG_ALGO = "SHA1withRSA";

    private String signedAlgorithmName;

    public byte[] signHashForWindows(byte[] hash, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("NONEwithRSA", "SunMSCAPI");
        signature.initSign(privateKey);
        signature.update(hash);
        return signature.sign();
    }

    // TODO: truyen algorithm tu client
    public byte[] signHash(byte[] hash, PrivateKey privateKey) throws Exception {
        DigestCreator digestCreator = new DigestCreator();
        hash = digestCreator.digestWithSHAInfo("sha1", hash);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(hash);
    }

    // TODO: truyen algorithm tu client
    public byte[] signData(byte[] data, PrivateKey privateKey) throws Exception {
        this.signedAlgorithmName = DEFAULT_SIG_ALGO;
        Signature signature = Signature.getInstance(DEFAULT_SIG_ALGO);
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    // TODO: new SAFENETProvider() dang truyen them provider cho Nhi dong, can resolve theo tung crypto token
    public byte[] signData(byte[] data, PrivateKey privateKey, String hashAlgorithm) throws Exception {
        Signature signature = null;
        hashAlgorithm = hashAlgorithm.trim().toLowerCase();
        hashAlgorithm = hashAlgorithm.replace("-", "");
        switch (hashAlgorithm) {
            case "sha1":
                signature = Signature.getInstance(DEFAULT_SIG_ALGO, new SAFENETProvider());
                signedAlgorithmName = "SHA1withRSA";
                break;
            case "sha256":
                signature = Signature.getInstance("SHA256withRSA", new SAFENETProvider());
                signedAlgorithmName = "SHA256withRSA";
                break;
            case "sha512":
                signature = Signature.getInstance("SHA512withRSA", new SAFENETProvider());
                signedAlgorithmName = "SHA512withRSA";
                break;
            default:
                throw new Exception("Algorithm not supported");
        }
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }

    public String getSignedAlgorithmName() {
        return signedAlgorithmName;
    }
}
