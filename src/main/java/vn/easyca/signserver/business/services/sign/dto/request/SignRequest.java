package vn.easyca.signserver.business.services.sign.dto.request;

import vn.easyca.signserver.business.services.sign.dto.TokenInfoDTO;
import vn.easyca.signserver.business.services.dto.OptionalDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SignRequest<T> {

    private final List<SignElement<T>> elements = new ArrayList<>();

    private TokenInfoDTO tokenInfoDTO;

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

    public void setTokenInfoDTO(TokenInfoDTO tokenInfoDTO) {
        this.tokenInfoDTO = tokenInfoDTO;
    }

    public List<SignElement<T>> getSignElements() throws Exception {
        if (elements == null || elements.size() == 0)
            throw  new Exception("have not element to sign");
        return elements;
    }

    public void Add(String key, T content , Date signDate, String signer){
        SignElement<T>  element = new SignElement<>();
        element.setContent(content);
        element.setSignDate(signDate);
        element.setSigner(signer);
        element.setKey(key);
        elements.add(element);
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
