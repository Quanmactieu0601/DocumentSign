package vn.easyca.signserver.core.dto;

public class ChangePinHsmUserRequest {

    private String masterKey;

    private String serial;

    private String oldPin;

    private String newPin;


    public ChangePinHsmUserRequest() {
    }

    public ChangePinHsmUserRequest(String masterKey, String serial, String oldPin, String newPin) {
        this.masterKey = masterKey;
        this.serial = serial;
        this.oldPin = oldPin;
        this.newPin = newPin;
    }

    public String getMasterKey() {
        return masterKey;
    }

    public void setMasterKey(String masterKey) {
        this.masterKey = masterKey;
    }

    public String getOldPin() {
        return oldPin;
    }

    public void setOldPin(String oldPin) {
        this.oldPin = oldPin;
    }

    public String getNewPin() {
        return newPin;
    }

    public void setNewPin(String newPin) {
        this.newPin = newPin;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }
}
