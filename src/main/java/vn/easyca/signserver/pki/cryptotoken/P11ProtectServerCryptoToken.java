package vn.easyca.signserver.pki.cryptotoken;


import au.com.safenet.crypto.provider.SAFENETProvider;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.BufferingContentSigner;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.pki.cryptotoken.error.CryptoTokenException;
import vn.easyca.signserver.pki.cryptotoken.error.InitCryptoTokenException;
import vn.easyca.signserver.pki.sign.utils.StringUtils;
import vn.easyca.signserver.webapp.config.Constants;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

/**
 * @author ThanhLD
 * Cau hinh den HSM ProtectServer
 */
@Component
public class P11ProtectServerCryptoToken implements CryptoToken {
    private KeyStore ks = null;
    private final HsmConfig hsmConfig;
    private String providerName;

    public P11ProtectServerCryptoToken(HsmConfig hsmConfig) {
        this.hsmConfig = hsmConfig;
    }

    public void initPkcs11() throws InitCryptoTokenException {
        if (ks == null) {
            String pkcs11Config = hsmConfig.getPkcs11Config();
            String modulePin = hsmConfig.getModulePin();
            if (pkcs11Config.isEmpty())
                throw new InitCryptoTokenException("Config is empty");
            if (modulePin.isEmpty())
                throw new InitCryptoTokenException("modulePin is required");
            // init provider
            ByteArrayInputStream confStream = new ByteArrayInputStream(pkcs11Config.getBytes());
            SAFENETProvider sAFENETProvider = new SAFENETProvider();
            Security.addProvider((Provider)sAFENETProvider);
            this.providerName = sAFENETProvider.getName();

            // load keystore
            char[] passphrase = modulePin.toCharArray();
            KeyStore ks = null;
            try {
                ks = KeyStore.getInstance("CRYPTOKI", providerName);
            } catch (KeyStoreException | NoSuchProviderException e) {
                throw new InitCryptoTokenException("Create KeyStore instance has error", e);
            }
            KeyStore.PasswordProtection pp = new KeyStore.PasswordProtection(passphrase);
            try {
                ks.load(null, pp.getPassword());
            } catch (Exception e) {
                throw new InitCryptoTokenException("loading keystore has error", e);
            }

            this.ks = ks;
        }
    }

    @Override
    public void initPkcs12(String p12Base64, String pin) {
        throw new NotImplementedException();
    }

    public String getProviderName() {
        return providerName;
    }

    public PrivateKey getPrivateKey(String alias) throws CryptoTokenException {
        if (this.ks == null)
            throw new CryptoTokenException("cryptoToken is not initialized");
        try {
            if (!ks.containsAlias(alias))
                throw new CryptoTokenException("Private key alias not found");
        } catch (KeyStoreException e) {
            throw new CryptoTokenException("KeyStore Exception", e);
        }
        try {
            return (PrivateKey) ks.getKey(alias, hsmConfig.getModulePin().toCharArray());
        } catch (Exception e) {
            throw new CryptoTokenException("load private key has error", e);
        }
    }

    public PublicKey getPublicKey(String alias) throws CryptoTokenException {
        if (this.ks == null)
            throw new CryptoTokenException("cryptoToken is not initialized");
        try {
            if (!ks.containsAlias(alias))
                throw new CryptoTokenException("Public key alias not found");
        } catch (KeyStoreException e) {
            throw new CryptoTokenException("get alias occurs error", e);
        }
        try {
            return ks.getCertificate(alias).getPublicKey();
        } catch (KeyStoreException e) {
            throw new CryptoTokenException("get public key occurs error", e);
        }
    }

    public Boolean containAlias(String alias) throws CryptoTokenException {
        if (this.ks == null)
            throw new CryptoTokenException("cryptoToken is not initialized");
        try {
            return ks.containsAlias(alias);
        } catch (KeyStoreException e) {
            throw new CryptoTokenException("get alias occurs error", e);
        }
    }

    public KeyPair genKeyPair(String alias, int keyLen) throws CryptoTokenException {
        if (this.ks == null)
            throw new CryptoTokenException("cryptoToken is not initialized");
        if (keyLen != 1024 && keyLen != 2048 && keyLen != 4096) {
            throw new CryptoTokenException("Only support keyLen as follows: 1024 2048 4096");
        }
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance("RSA", providerName);
        } catch (Exception e) {
            throw new CryptoTokenException("get keypair instance occurs error", e);
        }
        kpg.initialize(keyLen);
        KeyPair keyPair = kpg.generateKeyPair();
        //Gen self-signed cert, just is a temporary cert when we are waiting the right one from the CA
        X509Certificate cert;
        try {
            cert = genSelfSignedCert(alias, keyPair, providerName);
        } catch (Exception exception) {
            throw new CryptoTokenException("gen self signed Cert occurs has error ", exception);
        }
        try {
            ks.setKeyEntry(alias, keyPair.getPrivate(), hsmConfig.getModulePin().toCharArray(), new Certificate[]{cert});
        } catch (KeyStoreException e) {
            throw new CryptoTokenException("set entry key occurs error", e);
        }
        return keyPair;
    }

    public void installCert(String alias, X509Certificate cert) throws CryptoTokenException {
        if (this.ks == null)
            throw new CryptoTokenException("cryptoToken is not initialized");
        try {
            ks.setCertificateEntry(alias, cert);
        } catch (KeyStoreException e) {
            throw new CryptoTokenException("set CertificateEntry occurs error", e);
        }
    }


    public List<String> getAliases() throws CryptoTokenException {
        if (this.ks == null)
            throw new CryptoTokenException("cryptoToken is not initialized");
        List<String> aliases = new ArrayList<>();
        Enumeration<String> aliasEnum = null;
        try {
            aliasEnum = ks.aliases();
        } catch (KeyStoreException e) {
            throw new CryptoTokenException("get alias occurs error", e);
        }

        while (aliasEnum.hasMoreElements()) {
            aliases.add(aliasEnum.nextElement());
        }
        return aliases;
    }

    @Override
    public Object getConfiguration() throws CryptoTokenException {
        if (this.ks == null)
            throw new CryptoTokenException("cryptoToken is not initialized");
        return hsmConfig;
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

    @Override
    public Signature getSignatureInstance(String algorithm) throws ApplicationException {
        if (StringUtils.isNullOrEmpty(algorithm))
            throw new ApplicationException(-1, "Hash algorithm cannot be null or empty");

        algorithm = algorithm.trim().toUpperCase().replace("-", "");
        if (!Constants.HASH_ALGORITHM.contains(algorithm))
            throw new ApplicationException(-1, "Hash algorithm not correct");

        try {
            return Signature.getInstance(algorithm + "withRSA", new au.com.safenet.crypto.provider.slot0.SAFENETProvider());
        } catch (NoSuchAlgorithmException e) {
            throw new ApplicationException(-1, "Cannot get signature instance", e);
        }
    }
}
