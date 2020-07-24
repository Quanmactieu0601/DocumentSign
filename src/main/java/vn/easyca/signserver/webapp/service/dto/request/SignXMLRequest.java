package vn.easyca.signserver.webapp.service.dto.request;

import vn.easyca.signserver.webapp.service.dto.SignRequestDto;
import vn.easyca.signserver.webapp.service.dto.TokenInfo;

public class SignXMLRequest extends SignRequestDto {

    private final String xml;

    public SignXMLRequest(TokenInfo tokenInfo, String signer, String xml) {
        super(tokenInfo, signer);
        this.xml = xml;
    }
    public String getXml() {
        return xml;
    }
}
