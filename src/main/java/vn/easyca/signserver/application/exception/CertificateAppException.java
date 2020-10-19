package vn.easyca.signserver.application.exception;

public class CertificateAppException extends ApplicationException {
    public CertificateAppException(Throwable throwable) {
        this("Certificate has error! Please check serial and pin", throwable);
    }

    public CertificateAppException(String message, Throwable e) {
        super(ApplicationException.CERTIFICATE_ERROR_CODE, message, e);
    }

    public CertificateAppException(String msg) {
        super(ApplicationException.CERTIFICATE_ERROR_CODE, msg);
    }
}
