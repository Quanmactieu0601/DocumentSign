package vn.easyca.signserver.infrastructure.cryptotoken;

import org.springframework.stereotype.Component;
import vn.easyca.signserver.core.interfaces.CryptoTokenConnector;
import vn.easyca.signserver.pki.cryptotoken.Config;
import vn.easyca.signserver.pki.cryptotoken.CryptoToken;
import vn.easyca.signserver.pki.cryptotoken.P11CryptoToken;
import vn.easyca.signserver.pki.cryptotoken.error.InitCryptoTokenException;

@Component
public class HSMConnector implements CryptoTokenConnector {

    public static class HSMConnectorConfig{

        private String name = "EasyCAToken";
        private String lib = "C:\\Windows\\System32\\easyca_csp11_v1.dll";
        private String pin = "12345678";
        private String slot = null;
        private String attr = null;

        public String getAttr() {
            return attr;
        }

        public String getLib() {
            return lib;
        }

        public String getName() {
            return name;
        }

        public String getPin() {
            return pin;
        }

        public String getSlot() {
            return slot;
        }

        public HSMConnectorConfig(String name, String lib, String pin, String slot, String attr) {
            this.name = name;
            this.lib = lib;
            this.pin = pin;
            this.slot = slot;
            this.attr = attr;
        }
    }

    private static HSMConnectorConfig config;

    public static void Init(HSMConnectorConfig config) {
        if (config != null)
            HSMConnector.config = config;
    }

    @Override
    public CryptoToken getToken() throws CryptoTokenConnector.CryptoTokenConnectorException {

        if (config == null)
            throw new CryptoTokenConnectorException("hsm connector is not config");
        Config cryptoTokenConfig = new Config();
        cryptoTokenConfig.initPkcs11(config.getName(), config.getLib(), config.getPin());
        if (config.getSlot() != null)
            cryptoTokenConfig.withSlot(config.getSlot());
        if (config.getAttr() != null)
            cryptoTokenConfig.withAttributes(config.getAttr());
        P11CryptoToken p11CryptoToken = new P11CryptoToken();
        try {
            p11CryptoToken.init(cryptoTokenConfig);
        } catch (InitCryptoTokenException e) {
            throw new CryptoTokenConnectorException("Can not init crypto token", e);
        }
        return p11CryptoToken;
    }
}
