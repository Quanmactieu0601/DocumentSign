package vn.easyca.signserver.webapp.service.dto;

import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.domain.User;

public class CertificateGeneratedResult {

    private Certificate certificate;
    private String certSerial;
    private String certData;
    private String user;
    private String userPassword;

    public CertificateGeneratedResult() {
    }

    public CertificateGeneratedResult(Certificate certificate) {
        this.certificate = certificate;
    }

    public CertificateGeneratedResult(Certificate certificate, String user, String userPassword) {
        this.certificate = certificate;
        this.user = user;
        this.userPassword = userPassword;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
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

    public void setUser(String user) {
        this.user = user;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUser() {
        return user;
    }
}
