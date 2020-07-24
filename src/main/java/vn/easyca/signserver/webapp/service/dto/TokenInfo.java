package vn.easyca.signserver.webapp.service.dto;

public class TokenInfo {

    private String serial;

    private String pin;

    public TokenInfo(String serial, String pin) {
        this.serial = serial;
        this.pin = pin;
    }

    public TokenInfo(){

    }

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
