package vn.easyca.signserver.core.dto;

import org.apache.commons.lang3.StringUtils;
import vn.easyca.signserver.core.domain.CertPackage;
import vn.easyca.signserver.core.domain.OwnerInfo;
import vn.easyca.signserver.core.domain.RawCertificate;
import vn.easyca.signserver.core.domain.SubjectDN;

public class CertificateGenerateDTO {

    private String ou = "IT";
    private String t;
    private String l;
    private String o;
    private String s;
    private String c = "VN";
    private String cn;
    private String password;
    private String ownerId;
    private String ownerName;
    private String ownerEmail;
    private String ownerPhone;
    private int keyLen;
    private String certProfile;
    private String taxCode;
    private String identification;
    private int certProfileType;
    private RawCertificate rawCertificate; // used to register internal HSM

    public String getE() { return e; }

    public void setE(String e) { this.e = e; }

    public String getTelephoneNumber() { return telephoneNumber; }

    public void setTelephoneNumber(String telephoneNumber) { this.telephoneNumber = telephoneNumber; }

    private String e;
    private String telephoneNumber;
    public String getL() { return l; }

    public void setL(String l) { this.l = l; }

    public String getS() { return s; }

    public void setS(String s) { this.s = s; }

    public String getC() { return c; }

    public void setC(String c) { this.c = c; }

    public String getCn() { return cn; }

    public void setCn(String cn) { this.cn = cn; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getOwnerId() { return ownerId; }

    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public int getKeyLen() { return keyLen; }

    public void setKeyLen(int keyLen) { this.keyLen = keyLen; }

    public String getOu() { return ou; }

    public void setOu(String ou) { this.ou = ou; }

    public String getOwnerPhone() { return ownerPhone; }

    public String getOwnerEmail() { return ownerEmail; }

    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

    public void setOwnerPhone(String ownerPhone) { this.ownerPhone = ownerPhone; }

    public String getCertProfile() { return certProfile; }

    public void setCertProfile(String certProfile) { this.certProfile = certProfile; }

    public String getOwnerName() { return ownerName; }

    public SubjectDN getSubjectDN() { return new SubjectDN(cn, t, ou, o, l, s, c); }

    public OwnerInfo getOwnerInfo() { return new OwnerInfo(ownerId, ownerEmail, ownerPhone); }

    public CertPackage getCertPackage(String certMethod, int certType) { return new CertPackage(certMethod, certProfile, certType); }

    public String getTaxCode() { return taxCode; }

    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }

    public String getIdentification() { return identification; }

    public void setIdentification(String identification) { this.identification = identification; }

    public String getO() {
        return o;
    }

    public void setO(String o) {
        this.o = o;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public RawCertificate getRawCertificate() {
        return rawCertificate;
    }

    public void setRawCertificate(RawCertificate rawCertificate) {
        this.rawCertificate = rawCertificate;
    }

    public int getCertProfileType() { return certProfileType; }

    public void setCertProfileType(int certProfileType) { this.certProfileType = certProfileType; }

    public CertificateGenerateDTO() {
    }

    public CertificateGenerateDTO(String ou, String t, String l, String o, String s, String c, String cn, String ownerId, int keyLen) {
        this.ou = ou;
        this.t = t;
        this.l = l;
        this.o = o;
        this.s = s;
        this.c = c;
        this.cn = cn;
        this.ownerId = ownerId;
        this.keyLen = keyLen;
    }
}
