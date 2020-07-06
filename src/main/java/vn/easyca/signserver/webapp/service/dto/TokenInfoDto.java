package vn.easyca.signserver.webapp.service.dto;

public class SignatureInfoDto {

    private String serial;

    private String pin;

    public SignatureInfoDto(String serial, String pin) {
        this.serial = serial;
        this.pin = pin;
    }

    public String getPin() {
        return pin;
    }

    public String getSerial() {
        return serial;
    }
}
