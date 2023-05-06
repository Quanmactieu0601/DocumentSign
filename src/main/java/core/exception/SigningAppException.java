package core.exception;

public class SigningAppException extends ApplicationException {

    public SigningAppException(String message) {
        super(ApplicationException.SIGN_ERROR_CODE, message);
    }

    public SigningAppException(String message, Throwable throwable) {
        super(ApplicationException.SIGN_ERROR_CODE, message, throwable);
    }
}
