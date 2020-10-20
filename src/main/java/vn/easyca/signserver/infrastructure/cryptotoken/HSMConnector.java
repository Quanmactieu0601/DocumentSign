package vn.easyca.signserver.infrastructure.cryptotoken;

import org.springframework.stereotype.Component;
import vn.easyca.signserver.application.dependency.CryptoTokenConnector;
import vn.easyca.signserver.pki.cryptotoken.Config;
import vn.easyca.signserver.pki.cryptotoken.CryptoToken;
import vn.easyca.signserver.pki.cryptotoken.P11CryptoToken;
import vn.easyca.signserver.pki.cryptotoken.error.InitCryptoTokenException;

@Component
public class HSMConnector implements CryptoTokenConnector {

    @Override
    public CryptoToken getToken() throws CryptoTokenConnector.CryptoTokenConnectorException {
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
        } catch (InitCryptoTokenException e) {
            throw new CryptoTokenConnectorException("Can not init crypto token", e);
        }
        return p11CryptoToken;
    }
}
