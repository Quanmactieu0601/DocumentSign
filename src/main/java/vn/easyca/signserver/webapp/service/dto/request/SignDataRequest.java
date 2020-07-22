package vn.easyca.signserver.webapp.service.dto.request;
import vn.easyca.signserver.webapp.service.dto.SignRequestDto;
import vn.easyca.signserver.webapp.service.dto.TokenInfoDto;


public class SignDataRequest extends SignRequestDto {

    private String base64Data;

    private String hashAlgorithm;

    public SignDataRequest(TokenInfoDto tokenInfoDto, String base64Hash, String hashAlgorithm) {
        super(tokenInfoDto,null);
        this.base64Data = base64Hash;
        this.hashAlgorithm = hashAlgorithm;
    }
    public String getBase64Data() {
        return base64Data;
    }

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }
}
