package core.exception;

public class BadServiceInputAppException extends ApplicationException {

    public BadServiceInputAppException(String msg, Throwable throwable) {
        super(BAD_INPUT_ERROR_CODE, msg, throwable);
    }

    public BadServiceInputAppException(String msg) {
        super(BAD_INPUT_ERROR_CODE, msg);
    }
}
