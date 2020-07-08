package vn.easyca.signserver.webapp.service.dto.request;
import vn.easyca.signserver.webapp.service.dto.SignRequestDto;
import vn.easyca.signserver.webapp.service.dto.TokenInfoDto;
import vn.easyca.signserver.webapp.web.rest.vm.TokenInfoVM;

import java.util.Base64;


public class SignHashRequest extends SignRequestDto {

    private String base64Hash;

    private String hashAlgorithm;

    public SignHashRequest(TokenInfoDto tokenInfoDto, String base64Hash, String hashAlgorithm) {
        super(tokenInfoDto,null);
        this.base64Hash = base64Hash;
        this.hashAlgorithm = hashAlgorithm;
    }

    public byte[] getBytes(){
        return Base64.getDecoder().decode(base64Hash);
    }

    public String getBase64Hash() {
        return base64Hash;
    }

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }
}
