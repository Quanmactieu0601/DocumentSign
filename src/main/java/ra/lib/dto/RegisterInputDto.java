package ra.lib.dto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;

public class RegisterInputDto {

    private int activationCodeEnabled = 1;
    private int approvedIssueCert = 1;
    private int backedUpKey = 1;
    private String certMethod;
    private String certProfile;
    private int certProfileType;
    private int checkSendP12;
    private String cn;
    private String csr;
    private String customerEmail;
    private String customerPhone;
    private String dnsName = "";
    private String id;
    private String o;
    private String ou;
    private String p12RegisterPass = "";
    private String st;
    private String hashData;
    private String taxCode;
    private String identification;
    private String t;
    private String l;
    private String e;
    private String telephoneNumber;

    // Getter Methods

    public float getActivationCodeEnabled() {
        return activationCodeEnabled;
    }

    public float getApprovedIssueCert() {
        return approvedIssueCert;
    }

    public float getBackedUpKey() {
        return backedUpKey;
    }

    public String getCertMethod() {
        return certMethod;
    }

    public String getCertProfile() {
        return certProfile;
    }

    public float getCertProfileType() {
        return certProfileType;
    }

    public float getCheckSendP12() {
        return checkSendP12;
    }

    public String getCn() {
        return cn;
    }

    public String getCsr() {
        return csr;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public String getDnsName() {
        return dnsName;
    }

    public String getId() {
        return id;
    }

    public String getO() {
        return o;
    }

    public String getOu() {
        return ou;
    }

    public String getP12RegisterPass() {
        return p12RegisterPass;
    }

    public String getSt() {
        return st;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String genHash() throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        String content =
            certProfileType +
            certMethod +
            certProfile +
            customerEmail +
            customerPhone +
            cn +
            o +
            id +
            st +
            "373aa6451179fe58e5d205474044e379";
        md.update(content.getBytes());
        byte[] digest = md.digest();
        return hashData = DatatypeConverter.printHexBinary(digest);
    }

    public void setActivationCodeEnabled(int activationCodeEnabled) {
        this.activationCodeEnabled = activationCodeEnabled;
    }

    public void setApprovedIssueCert(int approvedIssueCert) {
        this.approvedIssueCert = approvedIssueCert;
    }

    public void setBackedUpKey(int backedUpKey) {
        this.backedUpKey = backedUpKey;
    }

    public void setCertMethod(String certMethod) {
        this.certMethod = certMethod;
    }

    public void setCertProfile(String certProfile) {
        this.certProfile = certProfile;
    }

    public void setCertProfileType(int certProfileType) {
        this.certProfileType = certProfileType;
    }

    public void setCheckSendP12(int checkSendP12) {
        this.checkSendP12 = checkSendP12;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public void setCsr(String csr) {
        this.csr = csr;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public void setDnsName(String dnsName) {
        this.dnsName = dnsName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setO(String o) {
        this.o = o;
    }

    public void setOu(String ou) {
        this.ou = ou;
    }

    public void setP12RegisterPass(String p12RegisterPass) {
        this.p12RegisterPass = p12RegisterPass;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getL() {
        return l;
    }

    public void setL(String l) {
        this.l = l;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getHashData() {
        return hashData;
    }

    public void setHashData(String hashData) {
        this.hashData = hashData;
    }
}
