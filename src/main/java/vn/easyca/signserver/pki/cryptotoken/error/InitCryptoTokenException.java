package vn.easyca.signserver.pki.cryptotoken.error;

public class InitCryptoTokenException extends CryptoTokenException {

    public InitCryptoTokenException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public InitCryptoTokenException(String message) {
        super(message);
    }
}
