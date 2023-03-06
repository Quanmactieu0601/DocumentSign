package vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO.request;

public class CertificateInfoRequest {

    private String username;
    private String certificates;
    private boolean certInfoEnabled;
    private boolean authInfoEnabled;
    private String serial;

    public CertificateInfoRequest() {
    }

    public CertificateInfoRequest(String agreementUUID, String certificates, boolean certInfoEnabled, boolean authInfoEnabled, String credentialID) {
        this.username = agreementUUID;
        this.certificates = certificates;
        this.certInfoEnabled = certInfoEnabled;
        this.authInfoEnabled = authInfoEnabled;
        this.serial = credentialID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCertificates() {
        return certificates;
    }

    public void setCertificates(String certificates) {
        this.certificates = certificates;
    }

    public boolean isCertInfoEnabled() {
        return certInfoEnabled;
    }

    public void setCertInfoEnabled(boolean certInfoEnabled) {
        this.certInfoEnabled = certInfoEnabled;
    }

    public boolean isAuthInfoEnabled() {
        return authInfoEnabled;
    }

    public void setAuthInfoEnabled(boolean authInfoEnabled) {
        this.authInfoEnabled = authInfoEnabled;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }
}
