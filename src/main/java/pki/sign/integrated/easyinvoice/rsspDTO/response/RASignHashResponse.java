package pki.sign.integrated.easyinvoice.rsspDTO.response;

import pki.sign.integrated.easyinvoice.rsspDTO.Response;

public class RASignHashResponse extends Response {

    private RsSignHashResponse data;

    public RsSignHashResponse getData() {
        return data;
    }

    public void setData(RsSignHashResponse data) {
        this.data = data;
    }
}
