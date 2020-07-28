package vn.easyca.signserver.webapp.web.rest.vm.request.sign;

public class SigningVM<T> {

    private String signer;
    private String signDate;
    private TokenVM tokenInfo;
    private OptionalVM optional;
    private T data;

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
