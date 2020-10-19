package vn.easyca.signserver.application.cryptotoken;

import vn.easyca.signserver.pki.cryptotoken.CryptoToken;


public interface CryptoTokenConnector {
    CryptoToken getToken() throws CryptoTokenConnectorException;
}
