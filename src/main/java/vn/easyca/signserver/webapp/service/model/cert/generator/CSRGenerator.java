package vn.easyca.signserver.webapp.service.model.cert.generator;

import sun.security.pkcs10.PKCS10;
import sun.security.x509.X500Name;
import vn.easyca.signserver.webapp.service.model.cert.data.CertProfile;

import javax.security.auth.x500.X500Principal;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.security.*;

class CSRGenerator {


    /**
     *
     * @param publicKey
     * @param privateKey
     * @param certProfile
     * @return CSR
     * @throws Exception
     */
    public String generatePKCS10(PublicKey publicKey, PrivateKey privateKey, CertProfile certProfile) throws Exception {
        // generate PKCS10 certificate request
        String sigAlg = "MD5WithRSA";
        PKCS10 pkcs10 = new PKCS10(publicKey);
        Signature signature = Signature.getInstance(sigAlg);
        signature.initSign(privateKey);
        X500Principal principal = generatePrincipal(certProfile);
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
        String res = new String(c);
        res = res.replace("-----BEGIN NEW CERTIFICATE REQUEST-----\r\n", "");
        res = res.replace("\r\n-----END NEW CERTIFICATE REQUEST-----\r\n", "");
        return res;
    }

    private X500Principal generatePrincipal(CertProfile certProfile) {
        StringBuilder principalBuilder = new StringBuilder();
        if (certProfile.getCn() != null)
            principalBuilder.append(String.format("CN=%s,", certProfile.getCn()));
        if (certProfile.getOu()!= null)
            principalBuilder.append(String.format("OU=%s,", certProfile.getOu()));
        if (certProfile.getO()!= null)
            principalBuilder.append(String.format("O=%s,", certProfile.getO()));
        if (certProfile.getL()!= null)
            principalBuilder.append(String.format("L=%s,", certProfile.getL()));
        if (certProfile.getS()!= null)
            principalBuilder.append(String.format("S=%s,", certProfile.getS()));
        if (certProfile.getC()!= null)
            principalBuilder.append(String.format("C=%s,", certProfile.getC()));
        String principal = principalBuilder.substring(0, principalBuilder.length() - 1);
        return new X500Principal(principal);

    }


}
