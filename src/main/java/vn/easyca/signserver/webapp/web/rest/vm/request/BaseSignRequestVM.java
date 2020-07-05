package vn.easyca.signserver.webapp.web.rest.vm.request;


import vn.easyca.signserver.webapp.web.rest.vm.SignatureInfoVM;

public class BaseSignRequestVM {

    private SignatureInfoVM signatureInfo;

    private String signer;

    private String signDate;

    public SignatureInfoVM getSignatureInfo() {
        return signatureInfo;
    }

    public void setSignatureInfo(SignatureInfoVM signatureInfo) {
        this.signatureInfo = signatureInfo;
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
}
