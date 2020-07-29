package vn.easyca.signserver.core.error.sign;

public class PDFSignException extends Exception{

    public PDFSignException(String message) {
        super(message);
    }

    public PDFSignException() {
        super("Không ký được file PDF");
    }
}
