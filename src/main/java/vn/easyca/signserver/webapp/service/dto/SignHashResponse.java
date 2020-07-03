package vn.easyca.signserver.webapp.service.dto;

import lombok.Getter;

import java.util.Base64;

@Getter
public class SignHashResponse {


    private  String signatureValue;

    private String certificate;

    public SignHashResponse(String signatureValue, String certificate) {
        this.signatureValue = signatureValue;
        this.certificate = certificate;
    }
}
