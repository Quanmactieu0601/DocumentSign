package vn.easyca.signserver.webapp.web.rest.vm.request.sign;


import vn.easyca.signserver.core.services.signing.dto.request.SigningRequest;
import vn.easyca.signserver.webapp.web.rest.mapper.SigningVMMapper;

public class SigningVM<T> {

    private String signer;
    private String signDate;
    private TokenVM tokenInfo;
    private OptionalVM optional;
    private T content;

    public String getSigner() {
        return signer;
    }

    public void setSigner(String signer) {
        this.signer = signer;
    }

    public String getSignDate() {
        return signDate;
    }

    public void setSignDate(String signDate) {
        this.signDate = signDate;
    }

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

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public <D> SigningRequest<D> getSigningRequest(Class<D> contentClass){
        SigningVMMapper<D,T> mapper = new SigningVMMapper<>();
        return mapper.map(this,contentClass);
    }


}
