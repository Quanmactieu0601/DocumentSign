package vn.easyca.signserver.core.dto;

public class ImportP12FileDTO {
    private String p12Base64;

    private String ownerId;

    private String pin;

    private int keyLen;

    private String aliasName;

    private String certProfile;

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

    public int getKeyLen() {
        return keyLen;
    }

    public void setKeyLen(int keyLen) {
        this.keyLen = keyLen;
    }

    public String getAliasName() {
        return aliasName;
    }
    public String getCertProfile() { return certProfile; }

    public void setCertProfile(String certProfile) { this.certProfile = certProfile; }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }
}
