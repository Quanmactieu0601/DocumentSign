package vn.easyca.signserver.business.services.signing.dto.request;

import vn.easyca.signserver.business.services.signing.dto.TokenInfoDTO;
import vn.easyca.signserver.business.services.dto.OptionalDTO;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignRequest<T> {

    private Map<String,SignElement<T>> signElements = new HashMap<>();

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

    public Map<String, SignElement<T>> getSignElements() throws Exception {
        if (signElements == null || signElements.size() == 0)
            throw  new Exception("have not element to sign");
        return signElements;
    }


    public void Add(String key, T content , Date signDate, String signer){
        SignElement<T>  element = new SignElement<>();
        element.setContent(content);
        element.setSignDate(signDate);
        element.setSigner(signer);
        signElements.put(key,element);
    }
    public void setSignElements(Map<String, SignElement<T>> signElements) {
        this.signElements = signElements;
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
