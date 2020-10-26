package vn.easyca.signserver.pki.cryptotoken.error;

public class CryptoTokenException extends Exception {
    public CryptoTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CryptoTokenException(String message) {
        super(message);
    }
}
