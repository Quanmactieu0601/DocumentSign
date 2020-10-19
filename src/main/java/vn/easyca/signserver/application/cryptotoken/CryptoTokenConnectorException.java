package vn.easyca.signserver.application.cryptotoken;
public class CryptoTokenConnectorException extends Exception {

    public CryptoTokenConnectorException(Throwable cause) {
        super(cause);
    }

    public CryptoTokenConnectorException(String message, Throwable cause) {
        super(message, cause);
    }

    public CryptoTokenConnectorException(String message) {
        super(message);
    }
}
