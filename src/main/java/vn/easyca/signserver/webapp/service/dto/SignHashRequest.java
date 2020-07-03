package vn.easyca.signserver.webapp.service.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Base64;

@Getter
@Setter
@Builder
public class SignHashRequest {

    private String base64Hash;

    private String serial;

    private String pin;

    private String hashAlgorithm;

    public byte[] getBytes(){
        return Base64.getDecoder().decode(base64Hash);
    }

}
