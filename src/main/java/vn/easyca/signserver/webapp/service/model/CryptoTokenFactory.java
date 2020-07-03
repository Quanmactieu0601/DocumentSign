package vn.easyca.signserver.webapp.service.model;

import vn.easyca.signserver.core.cryptotoken.Config;
import vn.easyca.signserver.core.cryptotoken.CryptoToken;
import vn.easyca.signserver.core.cryptotoken.P12CryptoToken;
import vn.easyca.signserver.webapp.domain.Certificate;

import java.io.ByteArrayInputStream;
import java.util.Base64;

public class CryptoTokenFactory {

    public static final String P11 = "PKCS11";

    public static final String P12 = "PKCS12";

    public static CryptoToken create(Certificate certificate, String pin) throws Exception {

        byte[] fileContent =Base64.getDecoder().decode(certificate.getRawData());
        P12CryptoToken p12CryptoToken = new P12CryptoToken();
        Config config = new Config();
        config.initPkcs12(new ByteArrayInputStream(fileContent), pin);
        p12CryptoToken.init(config);
        return p12CryptoToken;
    }


}
