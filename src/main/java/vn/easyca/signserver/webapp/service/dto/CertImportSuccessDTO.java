package vn.easyca.signserver.webapp.service.dto;

public class CertImportSuccessDTO {

    private String certId;

    private String personIdentity;

    public CertImportSuccessDTO() {
    }

    public CertImportSuccessDTO(String certId, String personIdentity) {
        this.certId = certId;
        this.personIdentity = personIdentity;
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
}
