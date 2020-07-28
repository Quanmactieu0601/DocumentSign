package vn.easyca.signserver.webapp.service.domain;

public class RawCertificate {
    private String serial;

    private String cert;

    public RawCertificate(String serial, String cert) {
        this.serial = serial;
        this.cert = cert;
    }

    public String getSerial() {
        return serial;
    }

    public String getCert() {
        return cert;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public void setCert(String cert) {
        this.cert = cert;
    }
}
