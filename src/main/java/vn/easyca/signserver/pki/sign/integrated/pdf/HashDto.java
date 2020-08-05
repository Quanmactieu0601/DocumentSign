package vn.easyca.signserver.pki.sign.integrated.pdf;

/**
 * Created by chen on 7/26/17.
 */
public class HashDto {
    private String b64Hash;
    private String requestId;

    public String getB64Hash() {
        return b64Hash;
    }

    public void setB64Hash(String b64Hash) {
        this.b64Hash = b64Hash;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public HashDto(String b64Hash, String requestId) {
        this.b64Hash = b64Hash;
        this.requestId = requestId;
    }
}
