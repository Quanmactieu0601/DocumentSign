package vn.easyca.signserver.core.dto.sign.newrequest;

import vn.easyca.signserver.core.dto.OptionalDTO;
import vn.easyca.signserver.core.dto.sign.TokenInfoDTO;

import java.util.List;

public class SigningRequest<T> {
    private List<T> signingRequestContents;
    private TokenInfoDTO tokenInfo;
    private OptionalDTO optional;


    public List<T> getSigningRequestContents() { return signingRequestContents; }

    public void setSigningRequestContents(List<T> signingRequestContents) { this.signingRequestContents = signingRequestContents; }

    public TokenInfoDTO getTokenInfo() {
        return tokenInfo;
    }

    public void setTokenInfo(TokenInfoDTO tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    public OptionalDTO getOptional() {
        return optional == null ? new OptionalDTO() : optional;
    }

    public void setOptional(OptionalDTO optional) {
        this.optional = optional;
    }

    @Override
    public String toString() {
        return "SigningRequest{" +
            "signingRequestContents=" + signingRequestContents +
            ", tokenInfo=" + tokenInfo +
            ", optional=" + optional +
            '}';
    }
}
