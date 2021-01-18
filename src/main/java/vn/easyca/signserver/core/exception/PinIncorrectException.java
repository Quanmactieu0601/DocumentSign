package vn.easyca.signserver.core.exception;

public class PinIncorrectException extends ApplicationException {
    public PinIncorrectException() {
        super(PIN_INCORRECT, "Keystore is not initialized, please check PIN number");
    }
}
