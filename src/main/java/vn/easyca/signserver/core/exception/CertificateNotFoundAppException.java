package vn.easyca.signserver.core.exception;

public class CertificateNotFoundAppException extends ApplicationException {

    public CertificateNotFoundAppException() {
        super(CERTIFICATE_NOT_FOUND, "The certificate is not found");
    }

}
