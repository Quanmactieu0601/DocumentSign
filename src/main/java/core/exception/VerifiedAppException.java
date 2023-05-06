package core.exception;

public class VerifiedAppException extends ApplicationException {

    public VerifiedAppException(Throwable e) {
        super(VERIFIED_EXCEPTION, "Verify signature error", e);
    }
}
