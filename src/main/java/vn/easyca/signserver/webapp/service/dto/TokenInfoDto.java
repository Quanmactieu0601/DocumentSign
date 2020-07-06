package vn.easyca.signserver.webapp.service.dto;

public class TokenInfoDto {

    private String serial;

    private String pin;

    public TokenInfoDto(String serial, String pin) {
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
