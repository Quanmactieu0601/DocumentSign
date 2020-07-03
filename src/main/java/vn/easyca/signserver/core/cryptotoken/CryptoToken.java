package vn.easyca.signserver.core.cryptotoken;

import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;

public interface CryptoToken {
    void init(Config config) throws Exception;

    PrivateKey getPrivateKey(String alias) throws Exception;

    PublicKey getPublicKey(String alias) throws Exception;

    Boolean containAlias(String alias) throws Exception;

    void genKeyPair(String alias, int keyLen) throws Exception;

    void installCert(String alias, X509Certificate cert) throws Exception;

    List<String> getAliases() throws Exception;

    Config getConfig() throws Exception;

    Certificate getCertificate(String alias) throws KeyStoreException;
}
