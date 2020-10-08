package vn.easyca.signserver.application.exception;

public class ApplicationException extends Exception {

    private int code;
    private String message;

    public ApplicationException(int code) {
        this.code = code;
    }

    public ApplicationException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApplicationException(String msg) {
        super(msg);
    }

    @Override
    public String toString() {
        return "ApplicationException{" +
            "code=" + code +
            ", message='" + message + '\'' +
            '}';
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
