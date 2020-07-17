package vn.easyca.signserver.webapp.service.model.generator;

import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import sun.security.pkcs10.PKCS10;
import sun.security.x509.CertificateExtensions;
import sun.security.x509.X500Name;

import javax.security.auth.x500.X500Principal;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.security.*;

public class CSRGenerator {



    /**
     * @param CN Common Name, is X.509 speak for the name that distinguishes
     *           the Certificate best, and ties it to your Organization
     * @param OU Organizational unit
     * @param O  Organization NAME
     * @param L  Location
     * @param S  State
     * @param C  Country
     * @return
     * @throws Exception
     */
    public String generatePKCS10(PublicKey publicKey,PrivateKey privateKey, String CN, String OU, String O,
                                  String L, String S, String C) throws Exception {
        // generate PKCS10 certificate request
        String sigAlg = "MD5WithRSA";
        PKCS10 pkcs10 = new PKCS10(publicKey);
        Signature signature = Signature.getInstance(sigAlg);
        signature.initSign(privateKey);
        X500Principal principal = generatePrincipal(CN, OU, O, L, S, C);
        X500Name x500name = null;
        x500name = new X500Name(principal.getEncoded());
        pkcs10.encodeAndSign(x500name, signature);
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(bs);
        pkcs10.print(ps);
        byte[] c = bs.toByteArray();

        try {
            if (ps != null)
                ps.close();
            if (bs != null)
                bs.close();
        } catch (Throwable th) {
        }
        return new String(c);
    }

    private X500Principal generatePrincipal(String CN, String OU, String O,
                                     String L, String S, String C) {
        StringBuilder principalBuilder = new StringBuilder();
        if (CN != null)
            principalBuilder.append(String.format("CN=%s,", CN));
        if (OU != null)
            principalBuilder.append(String.format("OU=%s,", OU));
        if (O != null)
            principalBuilder.append(String.format("O=%s,", O));
        if (L != null)
            principalBuilder.append(String.format("L=%s,", L));
        if (S != null)
            principalBuilder.append(String.format("S=%s,", S));
        if (C != null)
            principalBuilder.append(String.format("C=%s,", C));
        String principal = principalBuilder.substring(0,principalBuilder.length()-1);
        return new X500Principal(principal);

    }


}
