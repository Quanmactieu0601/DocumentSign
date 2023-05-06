package core.exception;

public class GenKeyPairAppException extends ApplicationException {

    public GenKeyPairAppException() {
        super(GEN_CSR_ERROR_CODE);
    }
}
