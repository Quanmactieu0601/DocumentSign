package vn.easyca.signserver.business.services.signing.dto.request;

import java.util.Date;

public class SignElement<T> {
    private String signer;
    private Date signDate;
    private T content;

    public String getSigner() {
        return signer;
    }

    public void setSigner(String signer) {
        this.signer = signer;
    }

    public Date getSignDate() {
        return signDate;
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
