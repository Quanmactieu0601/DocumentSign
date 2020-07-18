package vn.easyca.signserver.webapp.service.dto;

import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.domain.User;

public class CertificateGeneratedResult {

    private Certificate certificate;

    public CertificateGeneratedResult(Certificate certificate) {
        this.certificate = certificate;
    }

    private User user;
    private String userPassword;

    public CertificateGeneratedResult(Certificate certificate, User user, String userPassword) {
        this.certificate = certificate;
        this.user = user;
        this.userPassword = userPassword;
    }

    public CertificateGeneratedResult setUserInfo(User user,String userPass){
        this.user = user;
        this.userPassword = userPass;
        return this;
    }

    public CertificateGeneratedResult() {
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public User getUser() {
        return user;
    }
}
