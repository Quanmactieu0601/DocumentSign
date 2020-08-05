package vn.easyca.signserver.pki.sign.utils;

public class SignServerException extends Exception {

    private static final long serialVersionUID = 1L;

    public SignServerException(String message) {
        super(message);
    }

    public SignServerException(String message, Throwable e) {
        super(message, e);
    }
}
