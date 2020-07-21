package vn.easyca.signserver.webapp.service.dto;

import vn.easyca.signserver.webapp.web.rest.vm.GenCertificateVM;

import java.util.Date;

public class CertificateGeneratorDto {

    private String ou;
    private String l;
    private String s;
    private String c;
    private String cn;
    private String password;
    private String ownerId;
    private String ownerName;
    private String ownerEmail;
    private String ownerPhone;
    private int keyLen;
    private String certProfile;

    public CertificateGeneratorDto() {

    }

    public CertificateGeneratorDto(GenCertificateVM genCertificateVM) {
        this.setKeyLen(genCertificateVM.getKeyLen());
        this.setC(genCertificateVM.getC());
        this.setCn(genCertificateVM.getCn());
        this.setL(genCertificateVM.getL());
        this.setOu(genCertificateVM.getOu());
        this.setS(genCertificateVM.getS());
        this.setOwnerId(genCertificateVM.getOwnerId());
        this.setPassword(genCertificateVM.getPassword());
        this.setCertProfile(genCertificateVM.getCertProfile());
        this.setOwnerEmail(genCertificateVM.getOwnerEmail());
        this.setOwnerPhone(genCertificateVM.getOwnerPhone());
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

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getOu() {
        return ou;
    }

    public void setOu(String ou) {
        this.ou = ou;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getCertProfile() {
        return certProfile;
    }

    public void setCertProfile(String certProfile) {
        this.certProfile = certProfile;
    }

    public String getOwnerName() {
        return ownerName;
    }
}
