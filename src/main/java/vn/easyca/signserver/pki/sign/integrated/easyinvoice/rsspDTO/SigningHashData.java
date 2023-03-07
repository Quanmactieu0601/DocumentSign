package vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO;

public class SigningHashData {

    private String hashId;
    private String hashData;


    public SigningHashData() {
    }

    public String getHashId() {
        return hashId;
    }

    public void setHashId(String hashId) {
        this.hashId = hashId;
    }

    public String getHashData() {
        return hashData;
    }

    public void setHashData(String hashData) {
        this.hashData = hashData;
    }
}
