package vn.easyca.signserver.application.exception;

public class BadServiceInputException extends ApplicationException {

    public BadServiceInputException(String msg, Throwable throwable) {
        super(BAD_INPUT_ERROR_CODE, msg, throwable);
    }

    public BadServiceInputException(String msg) {
        super( BAD_INPUT_ERROR_CODE,msg);
    }
}
