package vn.easyca.signserver.application.exception;

public class GenKeyPairException extends ApplicationException {
    public GenKeyPairException() {
        super(CodeException.GEN_KEY_PAIR);
    }
}
