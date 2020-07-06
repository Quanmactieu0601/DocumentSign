package vn.easyca.signserver.webapp.service.dto.request;

import vn.easyca.signserver.webapp.service.dto.SignRequestDto;
import vn.easyca.signserver.webapp.service.dto.TokenInfoDto;

public class SignXMLRequest extends SignRequestDto {

    private final String xml;

    public SignXMLRequest(TokenInfoDto tokenInfoDto, String signer, String xml) {
        super(tokenInfoDto, signer);
        this.xml = xml;
    }
    public String getXml() {
        return xml;
    }
}
