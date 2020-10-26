package vn.easyca.signserver.core.interfaces;

import vn.easyca.signserver.pki.cryptotoken.CryptoToken;


public interface CryptoTokenConnector {
    class CryptoTokenConnectorException extends Exception {
        public CryptoTokenConnectorException(String message, Throwable cause) {
            super(message, cause);
        }

        public CryptoTokenConnectorException(String message) {
            super(message);
        }
    }
    CryptoToken getToken() throws CryptoTokenConnector.CryptoTokenConnectorException;
}
