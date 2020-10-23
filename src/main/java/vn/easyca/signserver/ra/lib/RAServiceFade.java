package vn.easyca.signserver.ra.lib;

import vn.easyca.signserver.ra.lib.api.RegisterCertificateApi;
import vn.easyca.signserver.ra.lib.authenticate.RAAuthenticate;

public class RAServiceFade {
    private static final String ACTION_AUTHENTICATE = "authenticate";
    private static final String ACTION_REGISTER = "ra/register-cert";

    private final RAConfig config;
    private final RAAuthenticate raAuthenticate;

    public RAServiceFade(RAConfig raConfig) {
        this.config = raConfig;
        this.raAuthenticate = new RAAuthenticate(
            config.getBaseUrl() + ACTION_AUTHENTICATE,
            config.getUserName(),
            config.getPassword());
    }

    public RegisterCertificateApi createRegisterCertificateApi() {
        return new RegisterCertificateApi(config.getBaseUrl() + ACTION_REGISTER, raAuthenticate);
    }
}
