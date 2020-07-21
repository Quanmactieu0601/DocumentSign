package vn.easyca.signserver.ca.service.api;

import vn.easyca.signserver.ca.service.Authenticate;

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
//
//    public static void main(String[] args) throws IOException, Unauthorized {
//        RegisterCertificateApi registerCertificateApi = CAFacadeApi.getInstance().init("http://172.16.10.66:8787/api/", "admin", "admin").createRegisterCertificateApi();
//        RegisterInputDto inputDto = new RegisterInputDto();
//        inputDto.setActivationCodeEnabled(1);
//        inputDto.setApprovedIssueCert(1);
//        inputDto.setCertMethod("SOFT_TOKEN");
//        inputDto.setCertProfile("T2OSB21Y");
//        inputDto.setCertProfile("T2OSB21Y");
//        inputDto.setCertProfileType(1);
//        inputDto.setCn("truonglx");
//        inputDto.setCustomerEmail("emailrac89@gmail.com");
//        inputDto.setId("173846902");
//        inputDto.setOu("IT");
//        inputDto.setSt("1");
//        registerCertificateApi.register(inputDto);
//
//
//    }
}
