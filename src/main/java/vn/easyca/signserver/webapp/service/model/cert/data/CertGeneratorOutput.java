package vn.easyca.signserver.webapp.service.model.cert.data;

public class CertGeneratorOutput {

    private final String certificate;
    private final String serial;

    public CertGeneratorOutput(String certificate, String serial) {
        this.certificate = certificate;
        this.serial = serial;
    }

    public String getCertificate() {
        return certificate;
    }

    public String getSerial() {
        return serial;
    }
}
