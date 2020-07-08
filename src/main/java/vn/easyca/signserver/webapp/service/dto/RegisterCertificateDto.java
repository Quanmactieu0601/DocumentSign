package vn.easyca.signserver.webapp.service.dto;

public class RegisterCertificateDto {

    private String p12Base64;

    private String ownerId;

    private String serial;

    private String pin;


    public String getP12Base64() {
        return p12Base64;
    }

    public void setP12Base64(String p12Base64) {
        this.p12Base64 = p12Base64;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
