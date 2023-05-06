package pki.sign.integrated.easyinvoice;

import pki.sign.integrated.easyinvoice.rsspDTO.ConfirmDataSignHash;
import study.web.rest.vm.request.sign.SigningVM;

public class SignThirdPartyRequest {

    private String username;
    private SigningVM<String> data;
    private ConfirmDataSignHash confirmData;

    public ConfirmDataSignHash getConfirmData() {
        return confirmData;
    }

    public void setConfirmData(ConfirmDataSignHash confirmData) {
        this.confirmData = confirmData;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public SigningVM<String> getData() {
        return data;
    }

    public void setData(SigningVM<String> data) {
        this.data = data;
    }

    public SignThirdPartyRequest() {}
}
