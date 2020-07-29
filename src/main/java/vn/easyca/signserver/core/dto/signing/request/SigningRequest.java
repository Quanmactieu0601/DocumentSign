package vn.easyca.signserver.core.dto.signing.request;

import vn.easyca.signserver.core.dto.signing.TokenInfoDTO;
import vn.easyca.signserver.core.dto.OptionalDTO;

import java.util.Date;

public class SigningRequest<T> {

    private T data;

    private TokenInfoDTO tokenInfoDTO;

    private String signer;

    private Date signDate = new Date();

    private OptionalDTO optional = new OptionalDTO();


    public TokenInfoDTO getTokenInfoDTO() {
        return tokenInfoDTO;
    }

    public OptionalDTO getOptional() {
        return optional;
    }

    public void setOptional(OptionalDTO optionalDTO) {
        this.optional = optionalDTO;
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

    public void setTokenInfoDTO(TokenInfoDTO tokenInfoDTO) {
        this.tokenInfoDTO = tokenInfoDTO;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
