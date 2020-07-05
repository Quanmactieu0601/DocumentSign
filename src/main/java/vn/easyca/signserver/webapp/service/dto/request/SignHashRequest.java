package vn.easyca.signserver.webapp.service.dto.request;
import vn.easyca.signserver.webapp.service.dto.SignRequestDto;

import java.util.Base64;


public class SignHashRequest extends SignRequestDto {

    private String base64Hash;

    private String hashAlgorithm;

    public SignHashRequest(String base64Hash, String hashAlgorithm) {
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
