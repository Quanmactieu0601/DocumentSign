package pki.cryptotoken.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class Pkcs12Utils {

    public static KeyPair createKeyPair(int keyLen) throws Exception {
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("get keypair instance occurs error", e);
        }

        kpg.initialize(keyLen);
        KeyPair keyPair = kpg.generateKeyPair();

        // private key
        keyPair.getPrivate();

        // public key
        keyPair.getPublic();

        //        byte[] encodedPublicKey = keyPair.getPrivate().getEncoded();
        //        String b64PublicKey = Base64.getEncoder().encodeToString(encodedPublicKey);
        return keyPair;
    }

    public static String createCSR(String subjectDN, KeyPair keyPair) throws CSRGenerator.CSRGeneratorException {
        String csr = new CSRGenerator().genCsr(subjectDN, null, keyPair.getPrivate(), keyPair.getPublic(), null, false, false);
        //        System.out.println(csr);
        return csr;
    }

    public static void saveToFile(KeyPair keyPair, String dir) {
        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(new FileOutputStream(dir + "rsaPublicKey"));
            dos.write(keyPair.getPublic().getEncoded());
            dos.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            dos = new DataOutputStream(new FileOutputStream(dir + "rsaPrivateKey"));
            dos.write(keyPair.getPrivate().getEncoded());
            dos.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (dos != null) try {
                dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void selfSignedCertificateToP12(
        String privateKeyFile,
        String certificateFile,
        String p12File,
        String alias,
        char[] password
    ) throws Exception {
        byte privateKeyData[] = Files.readAllBytes(Paths.get(privateKeyFile));
        byte certificateData[] = Files.readAllBytes(Paths.get(certificateFile));

        //Remove PEM header, footer and \n
        String privateKeyPEM = new String(privateKeyData, StandardCharsets.UTF_8);
        privateKeyPEM =
            privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----\n", "").replace("-----END PRIVATE KEY-----", "").replaceAll("\n", "");
        byte privateKeyDER[] = Base64.getDecoder().decode(privateKeyPEM);

        // Used to read User_privkey.pem file to get private key
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyDER);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(spec);

        //  Used to read user certificate
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        java.security.cert.Certificate cert = factory.generateCertificate(new ByteArrayInputStream(certificateData));

        //Create keystore, add entry with the provided alias and save
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(null);
        ks.setKeyEntry(alias, privateKey, password, new java.security.cert.Certificate[] { cert });
        OutputStream out = new FileOutputStream(p12File);
        ks.store(out, password);
        out.close();
        //        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //        ks.store(bos, password);
        //        bos.close();
        //        return bos.toByteArray();
    }

    public static byte[] selfSignedCertificateToP12v2(KeyPair keyPair, String cerData, String alias, String password) throws Exception {
        StringBuilder certBuilder = new StringBuilder();
        if (!cerData.contains("-----BEGIN CERTIFICATE-----")) {
            certBuilder.append("-----BEGIN CERTIFICATE-----").append("\n");
        }
        certBuilder.append(cerData);
        if (!cerData.contains("-----END CERTIFICATE-----")) {
            certBuilder.append("\n").append("-----END CERTIFICATE-----");
        }

        byte privateKeyData[] = keyPair.getPrivate().getEncoded();
        byte certificateData[] = certBuilder.toString().getBytes();

        //Remove PEM header, footer and \n
        //        String b64PrivateKey = Base64.getEncoder().encodeToString(privateKeyData);
        //        String privateKeyPEM = new String (privateKeyData, StandardCharsets.UTF_8);
        //        privateKeyPEM = privateKeyPEM.replace(
        //            "-----BEGIN PRIVATE KEY-----\n", "")
        //            .replace("-----END PRIVATE KEY-----", "")
        //            .replaceAll("\n", "");
        //        byte privateKeyDER[] = Base64.getDecoder().decode(privateKeyPEM);
        byte privateKeyDER[] = privateKeyData;

        // Used to read User_privkey.pem file to get private key
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyDER);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(spec);

        //  Used to read user certificate
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        java.security.cert.Certificate cert = factory.generateCertificate(new ByteArrayInputStream(certificateData));

        //Create keystore, add entry with the provided alias and save
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(null);
        ks.setKeyEntry(alias, privateKey, password.toCharArray(), new java.security.cert.Certificate[] { cert });

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ks.store(bos, password.toCharArray());
        bos.close();
        return bos.toByteArray();
    }
}
