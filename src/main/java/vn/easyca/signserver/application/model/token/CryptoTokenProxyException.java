package vn.easyca.signserver.application.model.token;

public class CryptoTokenProxyException extends Exception{
    public CryptoTokenProxyException(String message) {
        super(message);
    }

    public CryptoTokenProxyException(String message, Throwable cause) {
        super(message, cause);
    }
}
