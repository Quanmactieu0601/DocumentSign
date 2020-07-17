package vn.easyca.signserver.ca.service.api;

import vn.easyca.signserver.ca.service.Authenticate;

public class FacadeApi {

    private static final String ACTION_AUTHENTICATE = "";
    private static final String ACTION_REGISTER = "";
    private FacadeApi instance;
    private FacadeApi(){

    }
    private String baseUrl;
    private Authenticate authenticate;
    public void init(String baseUrl,String username,String password){
        this.baseUrl = baseUrl;
        this.authenticate = new Authenticate(baseUrl+ACTION_AUTHENTICATE,username,password);
    }
    public RegisterCertificateApi createRegisterCertificateApi(){
        RegisterCertificateApi registerCertificateApi = new RegisterCertificateApi(baseUrl+ACTION_REGISTER,authenticate);
        return registerCertificateApi;
    }

    public static void main(String[] args) {

    }
}
