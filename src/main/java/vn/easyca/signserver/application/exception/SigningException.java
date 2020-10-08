package vn.easyca.signserver.application.exception;

public class SigningException extends ApplicationException {

    public SigningException(String message) {
        super(CodeException.SIGN_EXCEPTION, message);
    }

    public SigningException() {
        super(CodeException.SIGN_EXCEPTION);
    }

}
