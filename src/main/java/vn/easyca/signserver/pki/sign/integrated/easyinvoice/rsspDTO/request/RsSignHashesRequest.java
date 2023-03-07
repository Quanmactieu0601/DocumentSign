package vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO.request;


import vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO.ConfirmDataSignHash;
import vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO.SigningHashData;

public class RsSignHashesRequest {

    private String serial;
    private String hashAlgo;
    private int confirmType;
    private SigningHashData[] hashData;
    private ConfirmDataSignHash confirmData;
    private int numHash;
    private String username;

    public RsSignHashesRequest() {
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getHashAlgo() {
        return hashAlgo;
    }

    public void setHashAlgo(String hashAlgo) {
        this.hashAlgo = hashAlgo;
    }

    public int getConfirmType() {
        return confirmType;
    }

    public void setConfirmType(int confirmType) {
        this.confirmType = confirmType;
    }

    public SigningHashData[] getHashData() {
        return hashData;
    }

    public void setHashData(SigningHashData[] hashData) {
        this.hashData = hashData;
    }

    public ConfirmDataSignHash getConfirmData() {
        return confirmData;
    }

    public void setConfirmData(ConfirmDataSignHash confirmData) {
        this.confirmData = confirmData;
    }

    public int getNumHash() {
        return numHash;
    }

    public void setNumHash(int numHash) {
        this.numHash = numHash;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
