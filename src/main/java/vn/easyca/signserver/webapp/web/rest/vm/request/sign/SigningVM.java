package vn.easyca.signserver.webapp.web.rest.vm.request.sign;

import vn.easyca.signserver.business.services.sign.dto.request.SignRequest;
import vn.easyca.signserver.webapp.web.rest.mapper.SignVMMapper;

import java.util.List;

public class SigningVM<T> {
    private TokenVM tokenInfo;
    private OptionalVM optional;
    private List<SignElementVM<T>> elements;

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

    public List<SignElementVM<T>> getElements() {
        return elements;
    }

    public void setElements(List<SignElementVM<T>> elements) {
        this.elements = elements;
    }

    public <D> SignRequest<D> getDTO(Class<D> classContent) {
        SignVMMapper<D, T> signVMMapper = new SignVMMapper<>();
        return signVMMapper.map(this, classContent);
    }
}
