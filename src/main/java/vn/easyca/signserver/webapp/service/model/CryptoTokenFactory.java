package vn.easyca.signserver.webapp.service.model;

import vn.easyca.signserver.core.cryptotoken.Config;
import vn.easyca.signserver.core.cryptotoken.CryptoToken;
import vn.easyca.signserver.core.cryptotoken.P11CryptoToken;
import vn.easyca.signserver.core.cryptotoken.P12CryptoToken;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.domain.CertificateType;
import vn.easyca.signserver.webapp.domain.TokenInfo;
import vn.easyca.signserver.webapp.encryption.CBCEncryption;
import vn.easyca.signserver.webapp.service.error.sign.InitTokenProxyException;
import vn.easyca.signserver.webapp.service.Encryption;

import java.io.ByteArrayInputStream;
import java.util.Base64;


/**
 * create CryptoToken
 */
public class CryptoTokenFactory {


    private Encryption encryption = new CBCEncryption();


    public CryptoToken resolveToken(Certificate certificate, String pin) throws Exception {

        String tokenType = certificate.getTokenType();
        switch (tokenType) {
            case CertificateType.PKCS_11:
                return resolveP11Token(certificate, pin);
            case CertificateType.PKCS_12:
                return resolveP12Token(certificate, pin);
            default:
                throw new InitTokenProxyException("Not found token type" + tokenType);
        }
    }

    private CryptoToken resolveP12Token(Certificate certificate, String pin) throws Encryption.EncryptionException {

        TokenInfo tokenInfo = certificate.getCertificateTokenInfo();
        String decryResult = encryption.decrypt(tokenInfo.getData());
        byte[] fileContent = Base64.getDecoder().decode(decryResult);
        P12CryptoToken p12CryptoToken = new P12CryptoToken();
        Config config = new Config();
        config.initPkcs12(new ByteArrayInputStream(fileContent), pin);
        try {
            p12CryptoToken.init(config);
            return p12CryptoToken;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return p12CryptoToken;
    }

    private CryptoToken resolveP11Token(Certificate certificate, String pin) {

        TokenInfo tokenInfo = certificate.getCertificateTokenInfo();
        P11CryptoToken p11CryptoToken = new P11CryptoToken();
        Config config = new Config();
        config.initPkcs11(tokenInfo.getName(), tokenInfo.getLibrary(), pin);
        try {
            p11CryptoToken.init(config);
            return p11CryptoToken;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return p11CryptoToken;
    }


}
