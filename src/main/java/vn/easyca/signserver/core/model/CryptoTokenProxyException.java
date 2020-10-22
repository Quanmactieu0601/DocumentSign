package vn.easyca.signserver.core.model;

public class CryptoTokenProxyException extends Exception{
    public CryptoTokenProxyException(String message) {
        super(message);
    }

    public CryptoTokenProxyException(String message, Throwable cause) {
        super(message, cause);
    }
}
