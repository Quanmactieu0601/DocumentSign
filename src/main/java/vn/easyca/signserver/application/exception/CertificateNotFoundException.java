package vn.easyca.signserver.application.exception;

public class CertificateNotFoundException extends ApplicationException {

    public CertificateNotFoundException() {
        super(CERTIFICATE_NOT_FOUND, "The certificate is not found");
    }

}
