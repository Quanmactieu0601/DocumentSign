package vn.easyca.signserver.core.services.signing.dto.response;

public class SigningDataResponse<T> {

    private final T signResult;

    private final String certificate;

    public SigningDataResponse(T signResult, String certificate) {
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
