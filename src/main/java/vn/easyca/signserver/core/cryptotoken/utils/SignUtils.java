package vn.easyca.signserver.core.cryptotoken.utils;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.xml.crypto.dsig.DigestMethod;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;

public class SignUtils {
    public byte[] signHash(byte[] data, String providerName, PrivateKey key, String hashAlg) throws Exception {
        byte[] hash = new DigestCreator().hash(data, hashAlg);
        if (hash == null)
            throw new Exception("Could not hash data");
        Cipher cipher = null;
        if (providerName == null || providerName.isEmpty())
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        else
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", providerName);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(hash);
    }

    private enum DigestAlgorithm {

        SHA1("SHA-1", "1.3.14.3.2.26", DigestMethod.SHA1), SHA256("SHA-256", "2.16.840.1.101.3.4.2.1",
                DigestMethod.SHA256), SHA512("SHA-512", "2.16.840.1.101.3.4.2.3", DigestMethod.SHA512);

        private String name;

        private String oid;

        private String xmlId;

        private DigestAlgorithm(String name, String oid, String xmlId) {
            this.name = name;
            this.oid = oid;
            this.xmlId = xmlId;
        }

        public static DigestAlgorithm getByName(String algoName) throws NoSuchAlgorithmException {
            if ("SHA-1".equals(algoName) || "SHA1".equals(algoName)) {
                return SHA1;
            }
            if ("SHA-256".equals(algoName)) {
                return SHA256;
            }
            if ("SHA-512".equals(algoName)) {
                return SHA512;
            }
            throw new NoSuchAlgorithmException("unsupported algo: " + algoName + ". Only support algorithm names: SHA-1, SHA-256, SHA-512");
        }

        public String getName() {
            return name;
        }

        public String getOid() {
            return oid;
        }

        public String getXmlId() {
            return xmlId;
        }

        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return new AlgorithmIdentifier(new DERObjectIdentifier(this.getOid()), new DERNull());
        }
    }

    private class DigestCreator {
        public byte[] hash(byte[] data, String hashAlg) throws Exception {
            DigestAlgorithm da = DigestAlgorithm.getByName(hashAlg);
            switch (da) {
                case SHA1:
                    return sha1(data);
                case SHA256:
                    return sha256(data);
                case SHA512:
                    return sha512(data);
            }
            return null;
        }

        private byte[] sha1(byte[] data) throws Exception {
            Security.addProvider(new BouncyCastleProvider());
            MessageDigest messageDigest = MessageDigest.getInstance(DigestAlgorithm.SHA1.getName(), "BC");
            messageDigest.update(data);
            byte[] digest = messageDigest.digest();
            ASN1ObjectIdentifier sha1oid_ = new ASN1ObjectIdentifier(DigestAlgorithm.SHA1.getOid());

            AlgorithmIdentifier sha1aid_ = new AlgorithmIdentifier(sha1oid_, null);
            DigestInfo di = new DigestInfo(sha1aid_, digest);
            return di.getEncoded();
        }

        private byte[] sha256(byte[] data) throws Exception {
            Security.addProvider(new BouncyCastleProvider());
            MessageDigest messageDigest = MessageDigest.getInstance(DigestAlgorithm.SHA256.getName(), "BC");
            messageDigest.update(data);
            byte[] digest = messageDigest.digest();
            DERObjectIdentifier sha1oid_ = new DERObjectIdentifier(DigestAlgorithm.SHA256.getOid());

            AlgorithmIdentifier sha1aid_ = new AlgorithmIdentifier(sha1oid_, null);
            DigestInfo di = new DigestInfo(sha1aid_, digest);
            return di.getEncoded();
        }

        private byte[] sha512(byte[] data) throws Exception {
            Security.addProvider(new BouncyCastleProvider());
            MessageDigest messageDigest = MessageDigest.getInstance(DigestAlgorithm.SHA512.getName(), "BC");
            messageDigest.update(data);
            byte[] digest = messageDigest.digest();
            DERObjectIdentifier sha1oid_ = new DERObjectIdentifier(DigestAlgorithm.SHA512.getOid());

            AlgorithmIdentifier sha1aid_ = new AlgorithmIdentifier(sha1oid_, null);
            DigestInfo di = new DigestInfo(sha1aid_, digest);
            return di.getEncoded();
        }
    }
}
