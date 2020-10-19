package vn.easyca.signserver.application.exception;

public class GenCSRException extends ApplicationException {
    public GenCSRException() {
        super(GEN_CSR_ERROR_CODE, "Can not generate csr exception");
    }
}
