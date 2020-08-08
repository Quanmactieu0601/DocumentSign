package vn.easyca.signserver.webapp.web.rest.vm.request.sign;

public class SignElementVM<T> {
    private String signer;
    private String signDate;
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

    public void setContent(T content) {
        this.content = content;
    }

    public T getContent() {
        return content;
    }
}
