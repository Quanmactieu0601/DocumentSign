package vn.easyca.signserver.core.dto.sign.newrequest;

import vn.easyca.signserver.core.dto.sign.TokenInfoDTO;

import java.util.List;

public class SigningRequest {
    private List<SigningRequestContent> signingRequestContents;
    private TokenInfoDTO tokenInfo;

    public List<SigningRequestContent> getSigningRequestContents() {
        return signingRequestContents;
    }

    public void setSigningRequestContents(List<SigningRequestContent> signingRequestContents) {
        this.signingRequestContents = signingRequestContents;
    }

    public TokenInfoDTO getTokenInfo() {
        return tokenInfo;
    }

    public void setTokenInfo(TokenInfoDTO tokenInfo) {
        this.tokenInfo = tokenInfo;
    }
}
