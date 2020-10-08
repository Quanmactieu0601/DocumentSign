package vn.easyca.signserver.application.exception;

public class TokenException extends ApplicationException {

    public TokenException(String msg) {
        super(CodeException.TOKEN_EXCEPTION, msg);
    }
    public TokenException() {
        super(CodeException.TOKEN_EXCEPTION);
    }
}
