package vn.easyca.signserver.application.dto;

public class OptionalDTO {


    private String hashAlgorithm = "SHA1";

    private String signatureAlgorithm= "SHA1WithRSA";

    private boolean returnInputData;


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

    public void setReturnInputData(boolean returnInputData) {
        this.returnInputData = returnInputData;
    }

    public boolean isReturnInputData() {
        return returnInputData;
    }
}
