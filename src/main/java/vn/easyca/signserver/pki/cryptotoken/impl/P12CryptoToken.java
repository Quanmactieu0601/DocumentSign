package vn.easyca.signserver.pki.cryptotoken.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.pki.cryptotoken.CryptoToken;
import vn.easyca.signserver.pki.cryptotoken.error.*;
import vn.easyca.signserver.pki.sign.utils.StringUtils;
import vn.easyca.signserver.webapp.config.Constants;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class P12CryptoToken implements CryptoToken {
    private KeyStore ks = null;
    private String modulePin = null;

    @Override
    public void initPkcs11() {
        throw new NotImplementedException();
    }

    public void initPkcs12(String p12Base64, String pin) throws InitCryptoTokenException {
        if (p12Base64 == null)
            throw new InitCryptoTokenException("Config is null");
        modulePin = pin;
        byte[] fileContent = Base64.getDecoder().decode(p12Base64);
        InputStream is = new ByteArrayInputStream(fileContent);
        if (modulePin == null || modulePin.isEmpty())
            throw new InitCryptoTokenException("Module pin is required");
        if (is == null)
            throw new InitCryptoTokenException("P12 input stream is required");

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
            return (PrivateKey) ks.getKey(alias, modulePin.toCharArray());
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

    @Override
    public Object getConfiguration() {
       return modulePin;
    }

    @Override
    public Certificate getCertificate(String alias) throws KeyStoreException {
        return ks.getCertificate(alias);
    }

    @Override
    public String getProviderName() {
        return null;
    }

    @Override
    public Signature getSignatureInstance(String algorithm) throws ApplicationException {
        if (StringUtils.isNullOrEmpty(algorithm))
            throw new ApplicationException(-1, "Hash algorithm cannot be null or empty");

        algorithm = algorithm.trim().toUpperCase().replace("-", "");
        if (!Constants.HASH_ALGORITHMS.contains(algorithm))
            throw new ApplicationException(-1, "Hash algorithm not correct");
        try {
            return Signature.getInstance(algorithm + "withRSA");
        } catch (NoSuchAlgorithmException e) {
            throw new ApplicationException(-1, "Cannot get signature instance", e);
        }
    }

    @Override
    public boolean isInitialized() throws ApplicationException {
        try {
            if (ks != null) {
                ks.aliases();
                return true;
            }
            return false;
        } catch (Exception ex) {
            throw new ApplicationException("Keystore is not initialized, please check PIN number");
        }
    }

    @Override
    public KeyStore getKeyStore() {
        return ks;
    }
}
