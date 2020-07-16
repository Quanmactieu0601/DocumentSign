package vn.easyca.signserver.webapp.web.rest.vm.request;


import vn.easyca.signserver.webapp.web.rest.vm.SigningOptionalVM;
import vn.easyca.signserver.webapp.web.rest.vm.TokenInfoVM;

public class SignRequestVM<T> {

    private TokenInfoVM tokenInfo;

    private String signer;

    private String signDate;

    private SigningOptionalVM optionalVM;

    private T data;

    public TokenInfoVM getTokenInfo() {
        return tokenInfo;
    }

    public void setTokenInfo(TokenInfoVM tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

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

    public void setOptionalVM(SigningOptionalVM optionalVM) {
        this.optionalVM = optionalVM;
    }

    public SigningOptionalVM getOptionalVM() {
        return optionalVM;
    }

    public T getData() {
        return data;
    }

}
