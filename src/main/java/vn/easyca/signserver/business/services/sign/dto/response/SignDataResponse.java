package vn.easyca.signserver.business.services.sign.dto.response;

public class SignDataResponse<T> {

    private final T signResult;

    private final String certificate;

    public SignDataResponse(T signResult, String certificate) {
        this.signResult = signResult;
        this.certificate = certificate;
    }

    public T getSignResult() {
        return signResult;
    }

    public String getCertificate() {
        return certificate;
    }

}
