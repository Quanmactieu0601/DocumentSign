package vn.easyca.signserver.webapp.web.rest.vm;

public class SigningOptionalVM {

    private String hashAlgorithm;

    private String signAlgorithm;


    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public String getSignAlgorithm() {
        return signAlgorithm;
    }

    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    public void setSignAlgorithm(String signAlgorithm) {
        this.signAlgorithm = signAlgorithm;
    }
}
