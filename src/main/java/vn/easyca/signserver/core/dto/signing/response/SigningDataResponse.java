package vn.easyca.signserver.core.dto.signing.response;

public class SigningDataResponse {


    private String signatureValue;

    private String certificate;

    public SigningDataResponse(String signatureValue, String certificate) {
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
