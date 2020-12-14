package vn.easyca.signserver.core.dto.sign.newrequest;

import java.util.Arrays;

public class SigningRequestContent {
    /***
     * document byte array which need to be sign
     */
    private byte[] data;

    /**
     * document name (identity data)
     */
    private String documentName;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    @Override
    public String toString() {
        return "SigningRequestContent{" +
            "data=" + Arrays.toString(data) +
            ", documentName='" + documentName + '\'' +
            '}';
    }
}
