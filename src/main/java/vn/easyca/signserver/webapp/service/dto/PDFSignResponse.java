package vn.easyca.signserver.webapp.service.dto;

import lombok.Getter;


@Getter
public class PDFSignResponse {

    private byte[] content;

    public PDFSignResponse(byte[] content) {
        this.content = content;
    }

}
