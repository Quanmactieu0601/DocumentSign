package vn.easyca.signserver.webapp.web.rest.vm.request.register;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class RegisterCertVM {
    @NotBlank(message = "Common Name is required. This field is customer's name.")
    private String commonName;
    private String locality;
    @NotBlank(message = "State is required. This field is customer's province code.")
    private String state;
    private String organization;
    private String ownerId;
    private int keyLen;
    private String certProfile;
    @NotNull(message = "certProfileType is required. Please set value 1 for personal profile and value 2 for organization profile.")
    private int certProfileType;
    @NotBlank(message = "email is required.")
    private String ownerEmail;
    @NotBlank(message = "phone is required.")
    private String ownerPhone;
    private String taxCode;
    private String identification;
    private String title;
    private String organizationUnit;
    private String phoneInCert;
    private String emailInCert;

    // file đăng ký kinh doanh
    byte[] businessRegistration;

    // cmnd người đại diện
    byte[] identityCardRepresent;

    // cmnd người đăng ký
    byte[] identityCardRegistrant;

    // giấy đăng ký người sử dụng
    byte[] userRegistrantForm;

    public String getCommonName() { return commonName; }

    public void setCommonName(String commonName) { this.commonName = commonName; }

    public String getLocality() { return locality; }

    public void setLocality(String locality) { this.locality = locality; }

    public String getState() { return state; }

    public void setState(String state) { this.state = state; }

    public String getOrganization() { return organization; }

    public void setOrganization(String organization) { this.organization = organization; }

    public String getOwnerId() { return ownerId; }

    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public int getKeyLen() { return keyLen; }

    public void setKeyLen(int keyLen) { this.keyLen = keyLen; }

    public String getCertProfile() { return certProfile; }

    public void setCertProfile(String certProfile) { this.certProfile = certProfile; }

    public int getCertProfileType() { return certProfileType; }

    public void setCertProfileType(int certProfileType) { this.certProfileType = certProfileType; }

    public String getOwnerEmail() { return ownerEmail; }

    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

    public String getOwnerPhone() { return ownerPhone; }

    public void setOwnerPhone(String ownerPhone) { this.ownerPhone = ownerPhone; }

    public String getTaxCode() { return taxCode; }

    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }

    public String getIdentification() { return identification; }

    public void setIdentification(String identification) { this.identification = identification; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getOrganizationUnit() { return organizationUnit; }

    public void setOrganizationUnit(String organizationUnit) { this.organizationUnit = organizationUnit; }

    public byte[] getBusinessRegistration() { return businessRegistration; }

    public void setBusinessRegistration(byte[] businessRegistration) { this.businessRegistration = businessRegistration; }

    public byte[] getIdentityCardRepresent() { return identityCardRepresent; }

    public void setIdentityCardRepresent(byte[] identityCardRepresent) { this.identityCardRepresent = identityCardRepresent; }

    public byte[] getIdentityCardRegistrant() { return identityCardRegistrant; }

    public void setIdentityCardRegistrant(byte[] identityCardRegistrant) { this.identityCardRegistrant = identityCardRegistrant; }

    public byte[] getUserRegistrantForm() { return userRegistrantForm; }

    public void setUserRegistrantForm(byte[] userRegistrantForm) { this.userRegistrantForm = userRegistrantForm; }

    public String getPhoneInCert() { return phoneInCert; }

    public void setPhoneInCert(String phoneInCert) { this.phoneInCert = phoneInCert; }

    public String getEmailInCert() { return emailInCert; }

    public void setEmailInCert(String emailInCert) { this.emailInCert = emailInCert; }
}
