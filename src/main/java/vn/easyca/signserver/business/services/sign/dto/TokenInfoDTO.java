package vn.easyca.signserver.business.services.sign.dto;

public class TokenInfoDTO {

    private String serial;

    private String pin;

    public String getPin() {
        return pin;
    }

    public String getSerial() {
        return serial;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }
}
