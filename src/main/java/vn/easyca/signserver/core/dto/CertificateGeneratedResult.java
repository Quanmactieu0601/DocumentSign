package vn.easyca.signserver.core.dto;

public class CertificateGeneratedResult {

    private String certSerial;
    private String certData;
    private String userName;
    private String userPassword;

    public CertificateGeneratedResult() {
    }

    public String getCertSerial() {
        return certSerial;
    }

    public void setCertSerial(String certSerial) {
        this.certSerial = certSerial;
    }

    public String getCertData() {
        return certData;
    }

    public void setCertData(String certData) {
        this.certData = certData;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUserName() {
        return userName;
    }
}
