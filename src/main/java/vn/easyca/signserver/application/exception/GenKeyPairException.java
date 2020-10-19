package vn.easyca.signserver.application.exception;

public class GenKeyPairException extends ApplicationException {
    public GenKeyPairException() {
        super(GEN_CSR_ERROR_CODE);
    }
}
