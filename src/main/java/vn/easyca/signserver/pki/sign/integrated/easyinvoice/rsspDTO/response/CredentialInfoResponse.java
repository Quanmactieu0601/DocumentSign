package vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO.response;


import vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO.CertificateAuthority;
import vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO.CertificateProfile;
import vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO.Types;

public class CredentialInfoResponse {
    private String credentialID;
    private String raw;

    private String serialNumber;

    private String status;

    private Types.AuthMode authMode;

    private int remainingSigningCounter;

    private String authorizationEmail;

    private String authorizationPhone;

    private String sharedMode;

    private String contractExpirationDate;

    private CertificateProfile certificateProfile;

    private CertificateAuthority certificateAuthority;

    private String notBefore;

    private String notAfter;


    public CredentialInfoResponse() {
    }

    public CredentialInfoResponse(String credentialID, String raw, String serialNumber, String status, Types.AuthMode authMode, int remainingSigningCounter, String authorizationEmail, String authorizationPhone, String sharedMode, String contractExpirationDate, CertificateProfile certificateProfile, CertificateAuthority certificateAuthority, String notBefore, String notAfter) {
        this.credentialID = credentialID;
        this.raw = raw;
        this.serialNumber = serialNumber;
        this.status = status;
        this.authMode = authMode;
        this.remainingSigningCounter = remainingSigningCounter;
        this.authorizationEmail = authorizationEmail;
        this.authorizationPhone = authorizationPhone;
        this.sharedMode = sharedMode;
        this.contractExpirationDate = contractExpirationDate;
        this.certificateProfile = certificateProfile;
        this.certificateAuthority = certificateAuthority;
        this.notBefore = notBefore;
        this.notAfter = notAfter;
    }

    public String getCredentialID() {
        return credentialID;
    }

    public void setCredentialID(String credentialID) {
        this.credentialID = credentialID;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Types.AuthMode getAuthMode() {
        return authMode;
    }

    public void setAuthMode(Types.AuthMode authMode) {
        this.authMode = authMode;
    }

    public int getRemainingSigningCounter() {
        return remainingSigningCounter;
    }

    public void setRemainingSigningCounter(int remainingSigningCounter) {
        this.remainingSigningCounter = remainingSigningCounter;
    }

    public String getAuthorizationEmail() {
        return authorizationEmail;
    }

    public void setAuthorizationEmail(String authorizationEmail) {
        this.authorizationEmail = authorizationEmail;
    }

    public String getAuthorizationPhone() {
        return authorizationPhone;
    }

    public void setAuthorizationPhone(String authorizationPhone) {
        this.authorizationPhone = authorizationPhone;
    }

    public String getSharedMode() {
        return sharedMode;
    }

    public void setSharedMode(String sharedMode) {
        this.sharedMode = sharedMode;
    }


    public CertificateProfile getCertificateProfile() {
        return certificateProfile;
    }

    public void setCertificateProfile(CertificateProfile certificateProfile) {
        this.certificateProfile = certificateProfile;
    }

    public String getNotBefore() {
        return notBefore;
    }

    public void setNotBefore(String notBefore) {
        this.notBefore = notBefore;
    }

    public String getNotAfter() {
        return notAfter;
    }

    public void setNotAfter(String notAfter) {
        this.notAfter = notAfter;
    }

    public String getContractExpirationDate() {
        return contractExpirationDate;
    }

    public void setContractExpirationDate(String contractExpirationDate) {
        this.contractExpirationDate = contractExpirationDate;
    }

    public CertificateAuthority getCertificateAuthority() {
        return certificateAuthority;
    }

    public void setCertificateAuthority(CertificateAuthority certificateAuthority) {
        this.certificateAuthority = certificateAuthority;
    }
}
