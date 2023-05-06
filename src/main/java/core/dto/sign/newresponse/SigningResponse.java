package core.dto.sign.newresponse;

import java.util.List;

public class SigningResponse {

    private List<SigningResponseContent> responseContentList;
    private String base64Certificate;

    public List<SigningResponseContent> getResponseContentList() {
        return responseContentList;
    }

    public void setResponseContentList(List<SigningResponseContent> responseContentList) {
        this.responseContentList = responseContentList;
    }

    public String getBase64Certificate() {
        return base64Certificate;
    }

    public void setBase64Certificate(String base64Certificate) {
        this.base64Certificate = base64Certificate;
    }
}
