package vn.easyca.signserver.ra;

import vn.easyca.signserver.ra.api.RegisterCertificateApi;
import vn.easyca.signserver.ra.authenticate.RAAuthenticate;

public class RAServiceFade {

    private static RAServiceFade instance;

    public static RAServiceFade getInstance() {
        if (instance == null)
            instance = new RAServiceFade();
        return instance;
    }

    private RAServiceFade() {
    }

    private static final String ACTION_AUTHENTICATE = "authenticate";
    private static final String ACTION_REGISTER = "ra/register-cert";
    private RAConfig config;
    private RAAuthenticate RAAuthenticate;

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
