package vn.easyca.signserver.webapp.web.rest.vm.request;


import vn.easyca.signserver.webapp.service.dto.TokenInfoDto;
import vn.easyca.signserver.webapp.web.rest.vm.SigningOptionalVM;
import vn.easyca.signserver.webapp.web.rest.vm.TokenInfoVM;

public class BaseSignRequestVM {

    private TokenInfoVM signatureInfo;

    private String signer;

    private String signDate;

    private SigningOptionalVM optionalVM;


    public TokenInfoVM getSignatureInfo() {
        return signatureInfo;
    }

    public void setSignatureInfo(TokenInfoVM signatureInfo) {
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

    public void setOptionalVM(SigningOptionalVM optionalVM) {
        this.optionalVM = optionalVM;
    }

    public SigningOptionalVM getOptionalVM() {
        return optionalVM;
    }


}
