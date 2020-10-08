package vn.easyca.signserver.application.exception;

public class CertificateNotFoundException extends ApplicationException {
    public CertificateNotFoundException(String msg) {
        super(CodeException.CERTIFICATE_NOT_FOUND, msg);
    }

    public CertificateNotFoundException() {
        super(CodeException.CERTIFICATE_NOT_FOUND, "The certificate is not found");
    }

}
