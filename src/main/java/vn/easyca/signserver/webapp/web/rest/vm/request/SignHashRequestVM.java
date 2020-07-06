package vn.easyca.signserver.webapp.web.rest.vm.request;

public class SignHashRequestVM extends BaseSignRequestVM{


    private String base64Hash;

    private String hashAlgorithm;

    public String getBase64Hash() {
        return base64Hash;
    }

    public void setBase64Hash(String base64Hash) {
        this.base64Hash = base64Hash;
    }

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }
}
