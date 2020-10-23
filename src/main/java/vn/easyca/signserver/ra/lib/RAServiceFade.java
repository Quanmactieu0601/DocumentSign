package vn.easyca.signserver.ra.lib;

import vn.easyca.signserver.ra.lib.api.RegisterCertificateApi;
import vn.easyca.signserver.ra.lib.authenticate.RAAuthenticate;

public class RAServiceFade {

    private static final String ACTION_AUTHENTICATE = "authenticate";
    private static final String ACTION_REGISTER = "ra/register-cert";

    private RAConfig config;
    private RAAuthenticate RAAuthenticate;
    public RAServiceFade(RAConfig raConfig) {
        this.config = raConfig;
    }

    public RAServiceFade init(RAConfig config) {
        this.config = config;
        this.RAAuthenticate = new RAAuthenticate(
            config.getBaseUrl() + ACTION_AUTHENTICATE,
            config.getUserName(),
            config.getPassword());
        return this;
    }

    public RegisterCertificateApi createRegisterCertificateApi() {
        return new RegisterCertificateApi(config.getBaseUrl() + ACTION_REGISTER, RAAuthenticate);
    }
}
