package vn.easyca.signserver.webapp.service.dto.request;
import vn.easyca.signserver.webapp.service.dto.SignRequestDto;
import vn.easyca.signserver.webapp.service.dto.TokenInfo;


public class SignDataRequest extends SignRequestDto {

    private String base64Data;

    public SignDataRequest(TokenInfo tokenInfo) {
        super(tokenInfo,null);
    }

    public String getBase64Data() {
        return base64Data;
    }

    public void setBase64Data(String base64Data) {
        this.base64Data = base64Data;
    }
}
