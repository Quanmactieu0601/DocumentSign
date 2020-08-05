package vn.easyca.signserver.ca.api.api;

import vn.easyca.signserver.ca.api.Authenticate;

public class CAFacadeApi {

    private static final String ACTION_AUTHENTICATE = "authenticate";
    private static final String ACTION_REGISTER = "ra/register-cert";
    private static CAFacadeApi instance;

    public static CAFacadeApi getInstance() {
        if (instance == null)
            instance = new CAFacadeApi();
        return instance;
    }

    private CAFacadeApi() {

    }

    private String baseUrl;
    private Authenticate authenticate;

    public CAFacadeApi init(String baseUrl, String username, String password) {
        this.baseUrl = baseUrl;
        this.authenticate = new Authenticate(baseUrl + ACTION_AUTHENTICATE, username, password);
        return this;
    }

    public RegisterCertificateApi createRegisterCertificateApi() {
        RegisterCertificateApi registerCertificateApi = new RegisterCertificateApi(baseUrl + ACTION_REGISTER, authenticate);
        return registerCertificateApi;
    }
    
}
