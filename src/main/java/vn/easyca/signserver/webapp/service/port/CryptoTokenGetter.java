package vn.easyca.signserver.webapp.service.port;


import vn.easyca.signserver.core.cryptotoken.CryptoToken;


public interface CryptoTokenGetter {

    public CryptoToken getToken() throws Exception;

}
