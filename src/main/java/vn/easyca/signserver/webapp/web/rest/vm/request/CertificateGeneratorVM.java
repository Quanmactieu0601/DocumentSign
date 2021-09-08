package vn.easyca.signserver.webapp.web.rest.vm.request;

import org.checkerframework.common.aliasing.qual.Unique;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class CertificateGeneratorVM {
    @NotBlank(message = "cn is required. This field is customer's name.")
    private String cn;
    private String l;
    @NotBlank(message = "s is required. This field is customer's province code.")
    private String s;
    @NotBlank(message = "o is required. This field is customer's name.")
    private String o;

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

    public String getO() {
        return o;
    }

    public void setO(String o) {
        this.o = o;
    }

    public String getL() {
        return l;
    }

    public void setL(String l) {
        this.l = l;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public int getKeyLen() {
        return keyLen;
    }

    public void setKeyLen(int keyLen) {
        this.keyLen = keyLen;
    }

    public String getCertProfile() {
        return certProfile;
    }

    public void setCertProfile(String certProfile) {
        this.certProfile = certProfile;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getTaxCode() { return taxCode; }

    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }

    public String getIdentification() { return identification; }

    public void setIdentification(String identification) { this.identification = identification; }

    public int getCertProfileType() { return certProfileType; }

    public void setCertProfileType(int certProfileType) { this.certProfileType = certProfileType; }

}
