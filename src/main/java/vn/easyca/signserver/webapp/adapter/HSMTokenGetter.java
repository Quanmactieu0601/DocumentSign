package vn.easyca.signserver.webapp.adapter;

import org.springframework.stereotype.Component;
import vn.easyca.signserver.core.services.CertGenService;
import vn.easyca.signserver.sign.core.cryptotoken.Config;
import vn.easyca.signserver.sign.core.cryptotoken.CryptoToken;
import vn.easyca.signserver.sign.core.cryptotoken.P11CryptoToken;

@Component
public class HSMTokenGetter implements CertGenService.CryptoTokenGetter {

    private final String TOKEN_NAME = "EasyCAToken";
    private final String TOKEN_LIB = "C:\\Windows\\System32\\easyca_csp11_v1.dll";
    private final String TOKEN_PIN = "12345678";

    @Override
    public CryptoToken getToken() throws Exception {
        Config config = new Config();
        config.initPkcs11(TOKEN_NAME,TOKEN_LIB,TOKEN_PIN);
        config = config.withSlot("1");
        P11CryptoToken p11CryptoToken  =new P11CryptoToken();
        p11CryptoToken.init(config);
        return p11CryptoToken;
    }
}
