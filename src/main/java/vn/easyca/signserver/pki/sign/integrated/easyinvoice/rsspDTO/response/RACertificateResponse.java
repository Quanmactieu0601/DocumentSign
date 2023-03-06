package vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO.response;


import vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO.Response;

public class RACertificateResponse extends Response {
    private CredentialInfoResponse data;

    public CredentialInfoResponse getData() {
        return data;
    }

    public void setData(CredentialInfoResponse data) {
        this.data = data;
    }
}
