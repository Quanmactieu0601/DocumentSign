package vn.easyca.signserver.application.exception;

public class BadServiceInputException extends ApplicationException {

    public BadServiceInputException(String msg) {
        super(CodeException.BAD_SERVICE_INPUT, msg);
    }
}
