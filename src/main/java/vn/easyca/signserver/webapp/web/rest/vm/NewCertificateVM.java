package vn.easyca.signserver.webapp.web.rest.vm;

public class NewCertificateVM {
    private String serial;
    private String cert;
    private String username;
    private String pass;

    public NewCertificateVM setCert(String serial, String cert) {
        this.serial = serial;
        this.cert = cert;
        return this;
    }

    public NewCertificateVM setUser(String username, String pass) {
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
