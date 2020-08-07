package vn.easyca.signserver.pki.cryptotoken;


import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.BufferingContentSigner;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;

public class P11CryptoToken implements CryptoToken {
    private KeyStore ks = null;
    private Config config = null;
    private String providerName;

    public void init(Config config) throws Exception {
        if (config == null)
            throw new Exception("Config is null");
        String pkcs11Config = config.getPkcs11Config();
        String modulePin = config.getModulePin();
        if (pkcs11Config.isEmpty())
            throw new Exception("pkcs11Config is required");
        if (modulePin.isEmpty())
            throw new Exception("modulePin is required");
        this.config = config;

        // init provider
        ByteArrayInputStream confStream = new ByteArrayInputStream(pkcs11Config.getBytes());
        Provider prov = new sun.security.pkcs11.SunPKCS11(confStream);
        Security.addProvider(prov);
        this.providerName = prov.getName();

        // set env variable for nCipher_ HSM (Thales)
        if (config.getLibrary().contains("libcknfast.so"))
            setEnvForNFastHSM();

        // load keystore
        char[] passphrase = modulePin.toCharArray();
        KeyStore ks = KeyStore.getInstance("PKCS11", prov);
        KeyStore.PasswordProtection pp = new KeyStore.PasswordProtection(passphrase);
        ks.load(null, pp.getPassword());
        this.ks = ks;
    }

    public String getProviderName() {
        return providerName;
    }

    public PrivateKey getPrivateKey(String alias) throws Exception {
        if (this.ks == null)
            throw new Exception("cryptoToken is not initialized");
        if (!ks.containsAlias(alias))
            throw new Exception("Private key alias not found");
        return (PrivateKey) ks.getKey(alias, config.getModulePin().toCharArray());
    }

    public PublicKey getPublicKey(String alias) throws Exception {
        if (this.ks == null)
            throw new Exception("cryptoToken is not initialized");
        if (!ks.containsAlias(alias))
            throw new Exception("Public key alias not found");
        return ks.getCertificate(alias).getPublicKey();
    }

    public Boolean containAlias(String alias) throws Exception {
        if (this.ks == null)
            throw new Exception("cryptoToken is not initialized");
        return ks.containsAlias(alias);
    }

    public KeyPair genKeyPair(String alias, int keyLen) throws Exception {
        if (this.ks == null)
            throw new Exception("cryptoToken is not initialized");
        if (keyLen != 1024 && keyLen != 2048 && keyLen != 4096) {
            throw new Exception("Only support keyLen as follows: 1024 2048 4096");
        }
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", providerName);
        kpg.initialize(keyLen);
        KeyPair keyPair = kpg.generateKeyPair();
        //Gen self-signed cert, just is a temporary cert when we are waiting the right one from the CA
        X509Certificate cert = genSelfSignedCert(alias, keyPair, providerName);
        //ks.setKeyEntry(alias, keyPair.getPrivate(), config.getModulePin().toCharArray(), new Certificate[]{cert});
        return keyPair;
    }

    public void installCert(String alias, X509Certificate cert) throws Exception {
        if (this.ks == null)
            throw new Exception("cryptoToken is not initialized");
        ks.setCertificateEntry(alias, cert);
    }

    public List<String> getAliases() throws Exception {
        if (this.ks == null)
            throw new Exception("cryptoToken is not initialized");
        List<String> aliases = new ArrayList<>();
        Enumeration<String> aliasEnum = ks.aliases();
        while (aliasEnum.hasMoreElements()) {
            aliases.add(aliasEnum.nextElement());
        }
        return aliases;
    }

    public Config getConfig() throws Exception {
        if (this.ks == null)
            throw new Exception("cryptoToken is not initialized");
        return this.config;
    }

    @Override
    public Certificate getCertificate(String alias) throws KeyStoreException {
        return ks.getCertificate(alias);
    }

    private X509Certificate genSelfSignedCert(String alias, KeyPair keyPair, String providerName) throws Exception {
        long validity = (long) 30 * 24 * 60 * 60 * 365;
        String myName = "CN=THIS IS TEMPORARY CERT FOR " + alias;

        long currentTime = new Date().getTime();
        Date firstDate = new Date(currentTime - 24 * 60 * 60 * 1000);
        Date lastDate = new Date(currentTime + validity * 1000);
        X500Name issuer = new X500Name(myName);
        BigInteger serno = BigInteger.valueOf(firstDate.getTime());
        PublicKey publicKey = keyPair.getPublic();

        X509v3CertificateBuilder cb = new JcaX509v3CertificateBuilder(issuer, serno, firstDate, lastDate, issuer, publicKey);
        ContentSigner signer = new BufferingContentSigner(new JcaContentSignerBuilder("SHA256WithRSA").setProvider(providerName).build(keyPair.getPrivate()), 20480);
        X509CertificateHolder cert = cb.build(signer);
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(cert.getEncoded()));
    }

    @SuppressWarnings({ "unchecked" })
    private void setEnvForNFastHSM() throws Exception {
        Map<String, String> newenv = new HashMap<>();
        newenv.put("CKNFAST_OVERRIDE_SECURITY_ASSURANCES", "tokenkeys");
        try {
            Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
            Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
            theEnvironmentField.setAccessible(true);
            Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
            env.putAll(newenv);
            Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
            theCaseInsensitiveEnvironmentField.setAccessible(true);
            Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
            cienv.putAll(newenv);
        } catch (NoSuchFieldException e) {
            Class[] classes = Collections.class.getDeclaredClasses();
            Map<String, String> env = System.getenv();
            for (Class cl : classes) {
                if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                    Field field = cl.getDeclaredField("m");
                    field.setAccessible(true);
                    Object obj = field.get(env);
                    Map<String, String> map = (Map<String, String>) obj;
                    map.clear();
                    map.putAll(newenv);
                }
            }
        }
    }
}
