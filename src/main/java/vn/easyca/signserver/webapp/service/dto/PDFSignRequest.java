package vn.easyca.signserver.webapp.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Builder

public class PDFSignRequest {


    @Getter
    @Setter
    private String serial;

    @Getter
    @Setter
    private byte[] content;

    @Setter
    private String signer;

    @Setter
    private Date signDate;

    @Getter
    @Setter
    private String pin;

    public String base64Hash;

    public Date getSignDate() {
        return signDate == null ? new Date() : signDate;
    }

    public String getSigner() {
        return signer == null ? "" : signer;
    }
}
