package vn.easyca.signserver.webapp.service.dto;

public class NewCertificateInfo {

    private String x509;
    private String serial;

    public NewCertificateInfo(String x509, String serial) {
        this.x509 = x509;
        this.serial = serial;
    }

    public String getSerial() {
        return serial;
    }

    public String getX509() {
        return x509;
    }

}
