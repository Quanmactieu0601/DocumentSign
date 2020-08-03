package vn.easyca.signserver.core.services.dto;

public class OptionalDTO {


    private String hashAlgorithm = "SHA1";
    private String signatureAlgorithm= "SHA1WithRSA";

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

}
