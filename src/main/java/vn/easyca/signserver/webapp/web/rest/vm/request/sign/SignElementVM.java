package vn.easyca.signserver.webapp.web.rest.vm.request.sign;

public class SignElementVM<T> {
    private String signer;
    private String signDate;
    private T content;
    private String key;

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

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "SignElementVM{" +
            "signer='" + signer + '\'' +
            ", signDate='" + signDate + '\'' +
            ", content=" + content +
            ", key='" + key + '\'' +
            '}';
    }
}
