package vn.easyca.signserver.pki.cryptotoken;

import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;

import vn.easyca.signserver.pki.cryptotoken.error.*;

public interface CryptoToken {
    void init(Config config) throws InitCryptoTokenException;

    PrivateKey getPrivateKey(String alias) throws CryptoTokenException;

    PublicKey getPublicKey(String alias) throws CryptoTokenException;

    Boolean containAlias(String alias) throws CryptoTokenException;

    KeyPair genKeyPair(String alias, int keyLen) throws CryptoTokenException;

    void installCert(String alias, X509Certificate cert) throws CryptoTokenException;

    List<String> getAliases() throws CryptoTokenException;

    Config getConfig() throws CryptoTokenException;

    Certificate getCertificate(String alias) throws KeyStoreException;

    String getProviderName() throws CryptoTokenException;
}
