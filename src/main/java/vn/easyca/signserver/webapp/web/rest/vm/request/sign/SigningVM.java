package vn.easyca.signserver.webapp.web.rest.vm.request.sign;

import vn.easyca.signserver.business.services.signing.dto.request.SignRequest;
import vn.easyca.signserver.webapp.web.rest.mapper.SignVMMapper;

import java.util.Map;

public class SigningVM<T> {
    private TokenVM tokenInfo;
    private OptionalVM optional;
    private Map<String, SignElementVM<T>> elements;

    public TokenVM getTokenInfo() {
        return tokenInfo;
    }

    public void setTokenInfo(TokenVM tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    public OptionalVM getOptional() {
        return optional;
    }

    public void setOptional(OptionalVM optional) {
        this.optional = optional;
    }

    public Map<String, SignElementVM<T>> getElements() {
        return elements;
    }

    public void setElements(Map<String, SignElementVM<T>> elements) {
        this.elements = elements;
    }

    public <D> SignRequest<D> getDTO(Class<D> classContent) {
        SignVMMapper<D, T> signVMMapper = new SignVMMapper<>();
        return signVMMapper.map(this, classContent);
    }
}
