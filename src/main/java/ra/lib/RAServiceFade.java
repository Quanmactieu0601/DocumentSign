package ra.lib;

import ra.lib.api.RegisterCertificateApi;
import ra.lib.authenticate.RAAuthenticate;

public class RAServiceFade {

    private static final String ACTION_AUTHENTICATE = "authenticate";
    private static final String ACTION_REGISTER = "ra/register-cert";
    private static final String ACTION_MULTIPLE_REGISTER = "p/register-certs";
    private final RAConfig config;
    private final RAAuthenticate raAuthenticate;

    public RAServiceFade(RAConfig raConfig) {
        this.config = raConfig;
        this.raAuthenticate = new RAAuthenticate(config.getBaseUrl() + ACTION_AUTHENTICATE, config.getUserName(), config.getPassword());
    }

    public RegisterCertificateApi createRegisterCertificateApi() {
        return new RegisterCertificateApi(config.getBaseUrl() + ACTION_REGISTER, raAuthenticate);
    }

    public RegisterCertificateApi createMultipleRegisterCertificateApi() {
        return new RegisterCertificateApi(config.getBaseUrl() + ACTION_MULTIPLE_REGISTER, raAuthenticate);
    }
}
