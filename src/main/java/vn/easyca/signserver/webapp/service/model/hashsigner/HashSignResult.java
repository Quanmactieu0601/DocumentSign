package vn.easyca.signserver.webapp.service.model.hashsigner;

import lombok.Getter;

@Getter
public class HashSignResult {

    private String hashAlgorithm;

    private String signAlgorithm;

    private String certificate;

    private String signatureValue;

    public HashSignResult(String certificate, String signatureValue) {
        this.certificate = certificate;
        this.signatureValue = signatureValue;
    }

}
