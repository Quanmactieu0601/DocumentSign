package vn.easyca.signserver.webapp.adapter;

import org.springframework.stereotype.Component;
import vn.easyca.signserver.business.services.CertGenService;
import vn.easyca.signserver.pki.cryptotoken.Config;
import vn.easyca.signserver.pki.cryptotoken.CryptoToken;
import vn.easyca.signserver.pki.cryptotoken.P11CryptoToken;
import vn.easyca.signserver.webapp.config.Constants;

@Component
public class HSMTokenGetter implements CertGenService.CryptoTokenGetter {

    @Override
    public CryptoToken getToken() throws Exception {
        Config config = new Config();
        config.initPkcs11(Constants.HSMConfig.NAME, Constants.HSMConfig.LIB, Constants.HSMConfig.PIN);
        config = config.withSlot("1");
        P11CryptoToken p11CryptoToken = new P11CryptoToken();
        p11CryptoToken.init(config);
        return p11CryptoToken;
    }
}
