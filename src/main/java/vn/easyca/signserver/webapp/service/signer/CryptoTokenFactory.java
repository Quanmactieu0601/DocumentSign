package vn.easyca.signserver.webapp.service.signer;

import vn.easyca.signserver.core.cryptotoken.Config;
import vn.easyca.signserver.core.cryptotoken.CryptoToken;
import vn.easyca.signserver.core.cryptotoken.P11CryptoToken;
import vn.easyca.signserver.core.cryptotoken.P12CryptoToken;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.domain.TokenInfo;
import vn.easyca.signserver.webapp.service.error.sign.InitTokenProxyException;
import vn.easyca.signserver.webapp.commond.encryption.Encryption;

import java.io.ByteArrayInputStream;
import java.util.Base64;


/**
 * create CryptoToken
 */
public class CryptoTokenFactory {


    public CryptoToken resolveToken(Certificate certificate, String pin) throws Exception {

        String tokenType = certificate.getTokenType();
        switch (tokenType) {
            case Certificate.PKCS_11:
                return resolveP11Token(certificate, pin);
            case Certificate.PKCS_12:
                return resolveP12Token(certificate, pin);
            default:
                throw new InitTokenProxyException("Not found token type" + tokenType);
        }
    }

    private CryptoToken resolveP12Token(Certificate certificate, String pin) throws Encryption.EncryptionException {

        TokenInfo tokenInfo = certificate.getCertificateTokenInfo();
        byte[] fileContent = Base64.getDecoder().decode(tokenInfo.getData());
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
