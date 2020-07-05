package vn.easyca.signserver.webapp.service.dto;

import java.util.Date;

public class SignRequestDto {

    private SignatureInfoDto signatureInfoDto;

    private String signer;

    private Date signDate;

    public SignatureInfoDto getSignatureInfoDto() {
        return signatureInfoDto;
    }

    public String getSigner() {
        return signer;
    }

    public Date getSignDate() {
        return signDate;
    }
}
