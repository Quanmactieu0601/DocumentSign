package vn.easyca.signserver.webapp.service.dto;

import vn.easyca.signserver.webapp.service.signer.SigningOptional;

import java.util.Date;

public class SignRequestDto {

    private TokenInfo tokenInfo;

    private String signer;

    private Date signDate = new Date();

    private SigningOptional optional = new SigningOptional();

    public SignRequestDto(TokenInfo tokenInfo, String signer) {
        this.tokenInfo = tokenInfo;
        this.signer = signer;
    }

    public TokenInfo getTokenInfo() {
        return tokenInfo;
    }

    public SigningOptional getOptional() {
        return optional;
    }

    public void setOptional(SigningOptional signingOptional) {
        this.optional = signingOptional;
    }

    public String getSigner() {
        return signer;
    }

    public Date getSignDate() {
        return signDate;
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }

    public void setSigner(String signer) {
        this.signer = signer;
    }

    public void setTokenInfo(TokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo;
    }
}
