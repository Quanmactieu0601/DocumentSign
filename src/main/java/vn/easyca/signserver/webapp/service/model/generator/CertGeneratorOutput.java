package vn.easyca.signserver.webapp.service.model.generator;

public class CertGeneratorOutput {

    private String certificate;
    private String serial;

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
