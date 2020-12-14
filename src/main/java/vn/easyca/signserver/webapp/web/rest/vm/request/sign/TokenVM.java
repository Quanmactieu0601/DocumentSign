package vn.easyca.signserver.webapp.web.rest.vm.request.sign;

public class TokenVM {
    private String serial;
    private String pin;

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getSerial() {
        return serial;
    }

    @Override
    public String toString() {
        return "TokenVM{" +
            "serial='" + serial + '\'' +
            ", pin='" + pin + '\'' +
            '}';
    }
}
