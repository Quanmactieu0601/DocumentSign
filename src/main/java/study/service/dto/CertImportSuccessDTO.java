package study.service.dto;

public class CertImportSuccessDTO {

    private String certId;

    private String personIdentity;

    private String message;

    private String serial;

    private boolean status;

    public CertImportSuccessDTO() {}

    public CertImportSuccessDTO(String certId, String personIdentity) {
        this.certId = certId;
        this.personIdentity = personIdentity;
    }

    public CertImportSuccessDTO(String personIdentity, String message, String serial) {
        this.personIdentity = personIdentity;
        this.message = message;
        this.serial = serial;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCertId() {
        return certId;
    }

    public void setCertId(String certId) {
        this.certId = certId;
    }

    public String getPersonIdentity() {
        return personIdentity;
    }

    public void setPersonIdentity(String personIdentity) {
        this.personIdentity = personIdentity;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }
}
