package pki.sign.integrated.easyinvoice.rsspDTO;

public class SignatureHashData extends SigningHashData {

    private String signature;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
