package vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO.response;

import vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO.SignatureHashData;

import java.util.List;

public class RsSignHashResponse {

    private int numSignature;
    private int remainingSigningCounter;

    private List<SignatureHashData> signatures;

    public RsSignHashResponse() {
    }

    public int getNumSignature() {
        return numSignature;
    }

    public void setNumSignature(int numSignature) {
        this.numSignature = numSignature;
    }

    public int getRemainingSigningCounter() {
        return remainingSigningCounter;
    }

    public void setRemainingSigningCounter(int remainingSigningCounter) {
        this.remainingSigningCounter = remainingSigningCounter;
    }

    public List<SignatureHashData> getSignatures() {
        return signatures;
    }

    public void setSignatures(List<SignatureHashData> signatures) {
        this.signatures = signatures;
    }
}
