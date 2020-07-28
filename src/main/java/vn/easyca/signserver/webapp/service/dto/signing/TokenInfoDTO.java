package vn.easyca.signserver.webapp.service.dto.signing;

public class TokenInfoDTO {

    private String serial;

    private String pin;

    public TokenInfoDTO(String serial, String pin) {
        this.serial = serial;
        this.pin = pin;
    }

    public TokenInfoDTO(){

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
