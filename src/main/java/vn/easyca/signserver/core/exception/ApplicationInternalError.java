package vn.easyca.signserver.core.exception;

public class ApplicationInternalError extends ApplicationException {

    public ApplicationInternalError(Throwable throwable) {
        super(SERVER_INTERNAL_ERROR_CODE, "Application internal error. please check log", throwable);
    }
}
