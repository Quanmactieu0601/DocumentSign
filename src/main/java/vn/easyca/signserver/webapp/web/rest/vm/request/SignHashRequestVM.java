package vn.easyca.signserver.webapp.web.rest.vm.request;

import vn.easyca.signserver.webapp.web.rest.vm.TokenInfoVM;

public class SignHashRequestVM {

    private String base64Hash;

    private String hashAlgorithm;

    private TokenInfoVM tokenInfo;

    public TokenInfoVM getTokenInfo() {
        return tokenInfo;
    }

    public String getBase64Hash() {
        return base64Hash;
    }


}
