package vn.easyca.signserver.core.cryptotoken;

import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class P12CryptoToken implements CryptoToken {
    private KeyStore ks = null;
    private Config config = null;

    public void init(Config config) throws Exception {
        if (config == null)
            throw new Exception("Config is null");
        String modulePin = config.getModulePin();
        InputStream is = config.getP12InputStream();
        if (modulePin == null || modulePin.isEmpty())
            throw new Exception("Module pin is required");
        if (is == null)
            throw new Exception("P12 input stream is required");
        this.config = config;

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(is, modulePin.toCharArray());
        this.ks = keyStore;
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

    @Override
    public KeyPair genKeyPair(String alias, int keyLen) throws Exception {
        throw new Exception("Method is not supported with PKCS12");
    }

    @Override
    public void installCert(String alias, X509Certificate cert) throws Exception {
        throw new Exception("Method is not supported with PKCS12");
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

    @Override
    public String getProviderName() throws Exception {
        throw new Exception("Method is not supported with PKCS12");
    }
}
