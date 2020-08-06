package vn.easyca.signserver.business.services.signing.dto.response;

public class SignResultElement {

    private String signature;
    private String inputData;

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
