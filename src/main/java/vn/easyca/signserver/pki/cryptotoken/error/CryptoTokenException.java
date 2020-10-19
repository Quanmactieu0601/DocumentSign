package vn.easyca.signserver.pki.cryptotoken.error;

public class CryptoTokenException extends Exception {
    public CryptoTokenException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public CryptoTokenException(String message) {
        super(message);
    }
}
