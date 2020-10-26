package vn.easyca.signserver.pki.cryptotoken;

import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import vn.easyca.signserver.pki.cryptotoken.error.*;

public class P12CryptoToken implements CryptoToken {
    private KeyStore ks = null;
    private Config config = null;

    public void init(Config config) throws InitCryptoTokenException {
        if (config == null)
            throw new InitCryptoTokenException("Config is null");
        String modulePin = config.getModulePin();
        InputStream is = config.getP12InputStream();
        if (modulePin == null || modulePin.isEmpty())
            throw new InitCryptoTokenException("Module pin is required");
        if (is == null)
            throw new InitCryptoTokenException("P12 input stream is required");
        this.config = config;

        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("PKCS12");
        } catch (KeyStoreException e) {
            throw new InitCryptoTokenException("get instance occurs error", e);
        }
        try {
            keyStore.load(is, modulePin.toCharArray());
        } catch (Exception e) {

        }
        this.ks = keyStore;
    }

    public PrivateKey getPrivateKey(String alias) throws CryptoTokenException {
        if (this.ks == null)
            throw new CryptoTokenException("cryptoToken is not initialized");
        try {
            if (!ks.containsAlias(alias))
                throw new CryptoTokenException("Private key alias not found");
        } catch (KeyStoreException e) {
            throw new CryptoTokenException("get alias occurs err", e);
        }
        try {
            return (PrivateKey) ks.getKey(alias, config.getModulePin().toCharArray());
        } catch (Exception e) {
            throw new CryptoTokenException("get private key occurs err", e);
        }
    }

    public PublicKey getPublicKey(String alias) throws CryptoTokenException {
        if (this.ks == null)
            throw new CryptoTokenException("cryptoToken is not initialized");
        try {
            if (!ks.containsAlias(alias))
                throw new CryptoTokenException("Public key alias not found");
        } catch (KeyStoreException e) {
            throw new CryptoTokenException("keystore get alias occurs error", e);
        }
        try {
            return ks.getCertificate(alias).getPublicKey();
        } catch (KeyStoreException e) {
            throw new CryptoTokenException("Keystore get certificate occurs error", e);
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

    @Override
    public KeyPair genKeyPair(String alias, int keyLen) throws CryptoTokenException {
        throw new CryptoTokenException("Method is not supported with PKCS12");
    }

    @Override
    public void installCert(String alias, X509Certificate cert) throws CryptoTokenException {
        throw new CryptoTokenException("Method is not supported with PKCS12");
    }

    public List<String> getAliases() throws CryptoTokenException {
        if (this.ks == null)
            throw new CryptoTokenException("cryptoToken is not initialized");
        List<String> aliases = new ArrayList<>();
        Enumeration<String> aliasEnum = null;
        try {
            aliasEnum = ks.aliases();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        while (aliasEnum.hasMoreElements()) {
            aliases.add(aliasEnum.nextElement());
        }
        return aliases;
    }

    public Config getConfig() throws CryptoTokenException {
        if (this.ks == null)
            throw new CryptoTokenException("cryptoToken is not initialized");
        return this.config;
    }

    @Override
    public Certificate getCertificate(String alias) throws KeyStoreException {
        return ks.getCertificate(alias);
    }

    @Override
    public String getProviderName() throws CryptoTokenException {
        throw new CryptoTokenException("Method is not supported with PKCS12");
    }
}
