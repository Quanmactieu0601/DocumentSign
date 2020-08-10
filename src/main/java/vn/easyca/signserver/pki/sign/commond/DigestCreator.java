package vn.easyca.signserver.pki.sign.commond;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.MessageDigest;
import java.security.Security;

/**
 * Created by chen on 7/24/17.
 */
public class DigestCreator {

    public byte[] hash(byte[] data, String hashAlgo) throws Exception {
        if (hashAlgo == null || hashAlgo.isEmpty() || hashAlgo.equalsIgnoreCase("sha1"))
            return sha1(data);
        else if (hashAlgo.equalsIgnoreCase("sha256"))
            return sha256(data);
        else if (hashAlgo.equalsIgnoreCase("sha512"))
            return sha512(data);
        throw new Exception("Hash Algorithm not support");
    }

    public byte[] sha1(byte[] data) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        MessageDigest messageDigest = MessageDigest.getInstance(DigestAlgorithm.SHA1.getName(), "BC");
        messageDigest.update(data);
        byte[] digest = messageDigest.digest();
        ASN1ObjectIdentifier sha1oid_ = new ASN1ObjectIdentifier(DigestAlgorithm.SHA1.getOid());

        AlgorithmIdentifier sha1aid_ = new AlgorithmIdentifier(sha1oid_, null);
        DigestInfo di = new DigestInfo(sha1aid_, digest);
        return di.getEncoded();
    }

    public byte[] digestWithSHAInfo(String hashAlgo, byte[] digest) throws Exception {
        ASN1ObjectIdentifier sha1oid_ = null;
        if (hashAlgo == null || hashAlgo.isEmpty())
            hashAlgo = "sha1";
        if (hashAlgo.equalsIgnoreCase("sha1"))
            sha1oid_ = new ASN1ObjectIdentifier(DigestAlgorithm.SHA1.getOid());
        else if (hashAlgo.equalsIgnoreCase("sha256"))
            sha1oid_ = new ASN1ObjectIdentifier(DigestAlgorithm.SHA256.getOid());
        else if (hashAlgo.equalsIgnoreCase("sha512"))
            sha1oid_ = new ASN1ObjectIdentifier(DigestAlgorithm.SHA512.getOid());
        if (sha1oid_ == null)
            throw new Exception("Not support hash Algorithm");
        AlgorithmIdentifier sha1aid_ = new AlgorithmIdentifier(sha1oid_, null);
        DigestInfo di = new DigestInfo(sha1aid_, digest);
        return di.getEncoded();
    }

    public byte[] digestWithSHA1Info(byte[] digest) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        ASN1ObjectIdentifier sha1oid_ = new ASN1ObjectIdentifier(DigestAlgorithm.SHA1.getOid());

        AlgorithmIdentifier sha1aid_ = new AlgorithmIdentifier(sha1oid_, null);
        DigestInfo di = new DigestInfo(sha1aid_, digest);
        return di.getEncoded();
    }

    public byte[] sha256(byte[] data) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        MessageDigest messageDigest = MessageDigest.getInstance(DigestAlgorithm.SHA256.getName(), "BC");
        messageDigest.update(data);
        byte[] digest = messageDigest.digest();
        DERObjectIdentifier sha1oid_ = new DERObjectIdentifier(DigestAlgorithm.SHA256.getOid());

        AlgorithmIdentifier sha1aid_ = new AlgorithmIdentifier(sha1oid_, null);
        DigestInfo di = new DigestInfo(sha1aid_, digest);
        return di.getEncoded();
    }

    public byte[] sha512(byte[] data) throws Exception {
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
