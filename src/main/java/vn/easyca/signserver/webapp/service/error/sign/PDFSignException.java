package vn.easyca.signserver.webapp.service.error.sign;

public class PDFSignException extends Exception{

    public PDFSignException(String message) {
        super(message);
    }

    public PDFSignException() {
        super("Không ký được file PDF");
    }
}
