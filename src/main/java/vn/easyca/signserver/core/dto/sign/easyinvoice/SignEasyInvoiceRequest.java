package vn.easyca.signserver.core.dto.sign.easyinvoice;

import vn.easyca.signserver.core.dto.OptionalDTO;
import vn.easyca.signserver.core.dto.sign.TokenInfoDTO;

import java.util.List;

public class SignEasyInvoiceRequest<T> {
    private List<T> signingRequestContents;
    private TokenInfoDTO tokenInfo;

    public List<T> getSigningRequestContents() { return signingRequestContents; }

    public void setSigningRequestContents(List<T> signingRequestContents) { this.signingRequestContents = signingRequestContents; }

    public TokenInfoDTO getTokenInfo() {
        return tokenInfo;
    }

    public void setTokenInfo(TokenInfoDTO tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    @Override
    public String toString() {
        return "SigningRequest{" +
            "signingRequestContents=" + signingRequestContents +
            ", tokenInfo=" + tokenInfo +
            '}';
    }
}
