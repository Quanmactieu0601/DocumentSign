package vn.easyca.signserver.application.dto.sign.response;

import java.util.Base64;

public class SignResultElement {

    private String base64Signature;

    private String inputData;

    private String key;

    public static SignResultElement  create(byte[] signature, String inputData, String key) {
        SignResultElement signResultElement = new SignResultElement();
        signResultElement.base64Signature = Base64.getEncoder().encodeToString(signature);
        signResultElement.inputData = inputData;
        signResultElement.key = key;
        return signResultElement;
    }

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public String getBase64Signature() {
        return base64Signature;
    }

    public void setBase64Signature(String base64Signature) {
        this.base64Signature = base64Signature;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
