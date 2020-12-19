package vn.easyca.signserver.core.dto.verification;

public class CertificateVfDTO {
    private String issuer;
    private String subjectDn;
    private String validFrom;
    private String validTo;
    private RevocationStatus revocationStatus;

    // status of certificate at the time of signing
    private CertStatus signTimeStatus;

    // status of certificate at current time
    private CertStatus currentStatus;

    private boolean isEasyCACert;

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getSubjectDn() {
        return subjectDn;
    }

    public void setSubjectDn(String subjectDn) {
        this.subjectDn = subjectDn;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    public String getValidTo() {
        return validTo;
    }

    public void setValidTo(String validTo) {
        this.validTo = validTo;
    }

    public CertStatus getSignTimeStatus() {
        return signTimeStatus;
    }

    public void setSignTimeStatus(CertStatus signTimeStatus) {
        this.signTimeStatus = signTimeStatus;
    }

    public CertStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(CertStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public boolean isEasyCACert() {
        return isEasyCACert;
    }

    public void setEasyCACert(boolean easyCACert) {
        isEasyCACert = easyCACert;
    }

    public RevocationStatus getRevocationStatus() {
        return revocationStatus;
    }

    public void setRevocationStatus(RevocationStatus revocationStatus) {
        this.revocationStatus = revocationStatus;
    }
}
