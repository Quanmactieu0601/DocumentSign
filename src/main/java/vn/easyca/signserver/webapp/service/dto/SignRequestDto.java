package vn.easyca.signserver.webapp.service.dto;

import java.util.Date;

public class SignRequestDto {

    private TokenInfoDto tokenInfoDto;

    private String signer;

    private Date signDate = new Date();

    public SignRequestDto(TokenInfoDto tokenInfoDto, String signer) {
        this.tokenInfoDto = tokenInfoDto;
        this.signer = signer;
    }

    public TokenInfoDto getTokenInfoDto() {
        return tokenInfoDto;
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

    public void setTokenInfoDto(TokenInfoDto tokenInfoDto) {
        this.tokenInfoDto = tokenInfoDto;
    }
}
