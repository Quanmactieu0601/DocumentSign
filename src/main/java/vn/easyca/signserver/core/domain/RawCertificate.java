package vn.easyca.signserver.core.domain;

public class RawCertificate {
    private String serial;

    private String cert;

    public RawCertificate(String serial, String cert) {
        this.serial = serial;
       setCert(cert);
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
        if (cert != null)
            cert = cert.replace("\n","");
        this.cert = cert;
    }
}
