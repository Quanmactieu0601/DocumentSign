package vn.easyca.signserver.webapp.service.dto.response;

public class SignDataResponse {


    private String signatureValue;

    private String certificate;

    public SignDataResponse(String signatureValue, String certificate) {
        this.signatureValue = signatureValue;
        this.certificate = certificate;
    }

    public String getSignatureValue() {
        return signatureValue;
    }

    public void setSignatureValue(String signatureValue) {
        this.signatureValue = signatureValue;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }
}
