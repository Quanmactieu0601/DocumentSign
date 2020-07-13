package vn.easyca.signserver.webapp.service.model.rawsigner;

public class RawSigningResult {

    private String hashAlgorithm;

    private String signAlgorithm;

    private String certificate;

    private String signatureValue;

    public RawSigningResult(String certificate, String signatureValue) {
        this.certificate = certificate;
        this.signatureValue = signatureValue;
    }

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    public String getSignAlgorithm() {
        return signAlgorithm;
    }

    public void setSignAlgorithm(String signAlgorithm) {
        this.signAlgorithm = signAlgorithm;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getSignatureValue() {
        return signatureValue;
    }

    public void setSignatureValue(String signatureValue) {
        this.signatureValue = signatureValue;
    }
}
