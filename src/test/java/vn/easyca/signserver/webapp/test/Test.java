package vn.easyca.signserver.webapp.test;

import org.bouncycastle.util.encoders.Base64;
import vn.easyca.signserver.pki.cryptotoken.Config;
import vn.easyca.signserver.pki.cryptotoken.P11CryptoToken;
import vn.easyca.signserver.pki.cryptotoken.P12CryptoToken;
import vn.easyca.signserver.pki.cryptotoken.utils.CSRGenerator;
import vn.easyca.signserver.pki.cryptotoken.utils.SignUtils;

import java.io.File;
import java.io.FileInputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

public class Test {
    public static void main(String[] args) throws Exception {
     gen();
  //       testGenCsrWithToken();
//        testGenCsrWithP12File();
        //testSignHashWithToken();
    }
    private static void gen() throws Exception {
        Config config = Config.build().initPkcs11("token", "C:\\Windows\\System32\\easyca_csp11_v1.dll", "12345678").withSlot("1");
        P11CryptoToken p11CryptoToken = new P11CryptoToken();
        try {
            p11CryptoToken.init(config);
        } catch (InitCryptoTokenException e) {
            e.printStackTrace();
        }
        p11CryptoToken.genKeyPair("truonglx",1024);

    }
    private static void testSignHashWithToken() throws Exception {
        Config config = Config.build().initPkcs11("token", "C:\\Windows\\System32\\easyca_csp11_v1.dll", "EasyCA").withSlot("1");
        P11CryptoToken p11CryptoToken = new P11CryptoToken();
        try {
            p11CryptoToken.init(config);
        } catch (InitCryptoTokenException e) {
            e.printStackTrace();
        }
        List<String> aliasList = p11CryptoToken.getAliases();
        String alias = aliasList.get(0);
        PrivateKey privateKey = p11CryptoToken.getPrivateKey(alias);
        SignUtils signer = new SignUtils();
        byte[] data = "abc".getBytes();
        byte[] signature = signer.signHash(data, null, privateKey, "SHA-1");
        System.out.println(Base64.toBase64String(signature));
    }

    private static void testGenCsrWithToken() throws Exception {
        Config config = Config.build().initPkcs11("token", "C:\\Windows\\System32\\easyca_csp11_v1.dll", "EasyCA").withSlot("1");
        P11CryptoToken p11CryptoToken = new P11CryptoToken();
        try {
            p11CryptoToken.init(config);
        } catch (InitCryptoTokenException e) {
            e.printStackTrace();
        }
        List<String> aliasList = p11CryptoToken.getAliases();
        String alias = aliasList.get(0);
        PrivateKey privateKey = p11CryptoToken.getPrivateKey(alias);
        PublicKey publicKey = p11CryptoToken.getPublicKey(alias);
        String providerName = p11CryptoToken.getProviderName();
        String testSubject = "CN=HoangTD, OU=IT, O=SDS, ST=HN, C=VN";
        CSRGenerator requestUtils = new CSRGenerator();
        String pkcs10Request = requestUtils.genCsr(testSubject, providerName, privateKey, publicKey, "SHA256withRSA", false, false);
        System.out.println(pkcs10Request);
    }

    private static void testGenCsrWithP12File() throws Exception {
        String filePath = "C:\\Users\\User\\Desktop\\tms2.p12";
        String filePin = "1";
        FileInputStream fis = new FileInputStream(new File(filePath));
        Config config = Config.build().initPkcs12(fis, filePin);
        P12CryptoToken p12CryptoToken = new P12CryptoToken();
        try {
            p12CryptoToken.init(config);
        } catch (InitCryptoTokenException e) {
            e.printStackTrace();
        }
        List<String> aliasList = p12CryptoToken.getAliases();
        for (String alias : aliasList) {
            System.out.println(alias);
        }
        String alias = aliasList.get(1);
        PrivateKey privateKey = p12CryptoToken.getPrivateKey(alias);
        PublicKey publicKey = p12CryptoToken.getPublicKey(alias);
        String providerName = "";
        String testSubject = "CN=HoangTD, OU=IT, O=SDS, ST=HN, C=VN";
        CSRGenerator requestUtils = new CSRGenerator();
        String pkcs10Request = requestUtils.genCsr(testSubject, providerName, privateKey, publicKey, "SHA256withRSA", false, false);
        System.out.println(pkcs10Request);
    }
}
