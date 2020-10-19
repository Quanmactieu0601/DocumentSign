package vn.easyca.signserver.infrastructure.cryptotoken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import vn.easyca.signserver.application.cryptotoken.CryptoTokenConnectorException;
import vn.easyca.signserver.application.cryptotoken.CryptoTokenConnector;
import vn.easyca.signserver.pki.cryptotoken.Config;
import vn.easyca.signserver.pki.cryptotoken.CryptoToken;
import vn.easyca.signserver.pki.cryptotoken.P11CryptoToken;

@Component
public class HSMConnector implements CryptoTokenConnector {

    private final Logger log = LoggerFactory.getLogger(HSMConnector.class);

    @Override
    public CryptoToken getToken() throws CryptoTokenConnectorException {
        TokenConfig tokenConfig = TokenConfig.getInstance();
        if (!tokenConfig.isInit())
            throw new CryptoTokenConnectorException("Config is not correct");
        Config cryptoTokenConfig = new Config();
        cryptoTokenConfig.initPkcs11(tokenConfig.getName(), tokenConfig.getLib(), tokenConfig.getPin());
        if (tokenConfig.getSlot() != null)
            cryptoTokenConfig.withSlot(tokenConfig.getSlot());
        if (tokenConfig.getAttr() != null)
            cryptoTokenConfig.withSlot(tokenConfig.getAttr());
        P11CryptoToken p11CryptoToken = new P11CryptoToken();
        try {
            p11CryptoToken.init(cryptoTokenConfig);
        } catch (Exception e) {
            throw new CryptoTokenConnectorException(e);
        } catch (InitCryptoTokenException e) {
            e.printStackTrace();
        }
        return p11CryptoToken;
    }
}
