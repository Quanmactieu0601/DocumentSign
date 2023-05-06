package core.exception;

public class GenCSRAppException extends ApplicationException {

    public GenCSRAppException() {
        super(GEN_CSR_ERROR_CODE, "Can not generate csr exception");
    }
}
