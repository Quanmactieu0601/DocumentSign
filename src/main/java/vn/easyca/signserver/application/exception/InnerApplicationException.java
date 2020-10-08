package vn.easyca.signserver.application.exception;

public class InnerApplicationException extends ApplicationException {

    public InnerApplicationException(String msg) {
        super(CodeException.INNER_EXCEPTION, msg);
    }
    public InnerApplicationException() {
        super(CodeException.INNER_EXCEPTION, "Process has error. please check log");
    }
}
