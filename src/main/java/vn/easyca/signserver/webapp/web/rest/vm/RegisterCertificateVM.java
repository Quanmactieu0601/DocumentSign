package vn.easyca.signserver.webapp.web.rest.vm;

public class RegisterCertificateVM {
    private String serial;
    private String cert;
    private String username;
    private String pass;

    public RegisterCertificateVM setCert(String serial, String cert) {
        this.serial = serial;
        this.cert = cert;
        return this;
    }

    public RegisterCertificateVM setUser(String username, String pass) {
        this.username = username;
        this.pass = pass;
        return this;
    }

    public String getSerial() {
        return serial;
    }

    public String getCert() {
        return cert;
    }

    public String getUsername() {
        return username;
    }

    public String getPass() {
        return pass;
    }
}
