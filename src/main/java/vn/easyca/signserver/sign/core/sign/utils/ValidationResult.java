package vn.easyca.signserver.sign.core.sign.utils;

public enum ValidationResult {
    INVALID  (0, "Signature invalid"),
    VALID    (1, "Signature valid"),
    NO_SIG   (2, "Not found signature"),
    ERROR    (3, "Verify failed"),
    BAD_INPUT(4, "Document format invalid");

    private int status;
    private String message;

    private ValidationResult(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
