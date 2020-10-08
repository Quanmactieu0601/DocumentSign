package vn.easyca.signserver.application.exception;

public class GenCSRException extends ApplicationException {
    public GenCSRException() {
        super(CodeException.GEN_CSR, "Can not generate csr exception");
    }
}
