package vn.easyca.signserver.webapp.service.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.easyca.signserver.webapp.service.dto.SignRequestDto;

import java.util.Date;

public class SignPDFRequest extends SignRequestDto {

    private byte[] content;

    public SignPDFRequest(byte[] content) {
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }
}
