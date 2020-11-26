package vn.easyca.signserver.core.dto.sign.request;

import vn.easyca.signserver.core.dto.sign.TokenInfoDTO;
import vn.easyca.signserver.core.dto.OptionalDTO;
import vn.easyca.signserver.webapp.config.Constants;

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
        return optional == null ? new OptionalDTO() : optional;
    }

    public void setOptional(OptionalDTO optionalDTO) {
        this.optional = optionalDTO;
    }

    public void setTokenInfoDTO(TokenInfoDTO tokenInfoDTO) {
        this.tokenInfoDTO = tokenInfoDTO;
    }

    public List<SignElement<T>> getSignElements() {
        return elements;
    }

    public void Add(String key, T content, Date signDate, String signer) {
        SignElement<T> element = new SignElement<>();
        element.setContent(content);
        element.setSignDate(signDate);
        element.setSigner(signer);
        element.setKey(key);
        elements.add(element);
    }

    public String getHashAlgorithm() {
        String result = getOptional().getHashAlgorithm();
        if (result == null || result.isEmpty())
            result = Constants.HASH_ALGORITHM.DEFAULT_HASH_ALGORITHM;
        return result;
    }

    public boolean isReturnInputData() {
        return optional != null && optional.isReturnInputData();
    }

}
