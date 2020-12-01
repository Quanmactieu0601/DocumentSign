package vn.easyca.signserver.webapp.web.rest.vm.request;

public class P12ImportVM {

    private String p12Base64;

    private String ownerId;

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

    @Override
    public String toString() {
        return "P12ImportVM{" +
            "p12Base64='" + p12Base64 + '\'' +
            ", ownerId='" + ownerId + '\'' +
            ", pin='" + pin + '\'' +
            '}';
    }
}
