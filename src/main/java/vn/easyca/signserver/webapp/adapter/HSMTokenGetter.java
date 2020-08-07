package vn.easyca.signserver.webapp.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import vn.easyca.signserver.business.services.CertGenService;
import vn.easyca.signserver.pki.cryptotoken.Config;
import vn.easyca.signserver.pki.cryptotoken.CryptoToken;
import vn.easyca.signserver.pki.cryptotoken.P11CryptoToken;
import vn.easyca.signserver.webapp.config.Constants;

@Component
public class HSMTokenGetter implements CertGenService.CryptoTokenGetter {
    private final Logger log = LoggerFactory.getLogger(HSMTokenGetter.class);

    @Override
    public CryptoToken getToken() throws Exception {
        Config config = new Config();
        config.initPkcs11(Constants.HSMConfig.NAME, Constants.HSMConfig.LIB, Constants.HSMConfig.PIN);
        config = config.withSlot(Constants.HSMConfig.SLOT);
        config = config.withAttributes(Constants.HSMConfig.ATTRIBUTES);
        P11CryptoToken p11CryptoToken = new P11CryptoToken();
        try {
            p11CryptoToken.init(config);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return p11CryptoToken;
    }
}
