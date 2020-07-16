package vn.easyca.signserver.webapp.service.config;


import vn.easyca.signserver.core.cryptotoken.Config;

public class DomainConfigRegistration {

    private static DomainConfigRegistration instance;

    public static DomainConfigRegistration getInstance() {
        if (instance == null)
            instance = new DomainConfigRegistration();
        return instance;
    }

    private DomainConfigRegistration() {
    }

    private void init(String cfg) {
    }

    public Config getPKC11ConfigForGenKeyPair() {
        Config config = new Config();
        config.initPkcs11("token", "C:\\Windows\\System32\\easyca_csp11_v1.dll", "12345678");
        return config;
    }
}
