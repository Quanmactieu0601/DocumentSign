package vn.easyca.signserver.business.services.signing.dto.request;

import vn.easyca.signserver.business.services.signing.dto.TokenInfoDTO;
import vn.easyca.signserver.business.services.dto.OptionalDTO;

import java.util.Date;

public class SigningRequest<T> {

    private T content;

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

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public String getHashAlgorithm() {
        String result = getOptional().getHashAlgorithm();
        if (result == null || result.isEmpty())
            result = "SHA1";
        return result;
    }

    public boolean isReturnInputData(){
        return optional != null && optional.isReturnInputData();
    }

}
