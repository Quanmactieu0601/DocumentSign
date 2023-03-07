package vn.easyca.signserver.pki.sign.integrated.easyinvoice;

import vn.easyca.signserver.webapp.web.rest.vm.request.sign.SigningVM;

public class SignEasyInvoiceRequest {
    private String username;
    private SigningVM<String> data;

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

    public SignEasyInvoiceRequest() {
    }
}
