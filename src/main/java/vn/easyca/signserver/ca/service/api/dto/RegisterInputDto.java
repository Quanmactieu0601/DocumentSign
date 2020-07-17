package vn.easyca.signserver.ca.service.api.dto; /* created by truonglx  on 7/16/20 */

public class RegisterInputDto {

    private float activationCodeEnabled;
    private float approvedIssueCert;
    private float backedUpKey;
    private String certMethod;
    private String certProfile;
    private float certProfileType;
    private float checkSendP12;
    private String cn;
    private String csr;
    private String customerEmail;
    private String customerPhone;
    private String dnsName;
    private String id;
    private String o;
    private String ou;
    private String p12RegisterPass;
    private String st;


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

    // Setter Methods

    public void setActivationCodeEnabled(float activationCodeEnabled) {
        this.activationCodeEnabled = activationCodeEnabled;
    }

    public void setApprovedIssueCert(float approvedIssueCert) {
        this.approvedIssueCert = approvedIssueCert;
    }

    public void setBackedUpKey(float backedUpKey) {
        this.backedUpKey = backedUpKey;
    }

    public void setCertMethod(String certMethod) {
        this.certMethod = certMethod;
    }

    public void setCertProfile(String certProfile) {
        this.certProfile = certProfile;
    }

    public void setCertProfileType(float certProfileType) {
        this.certProfileType = certProfileType;
    }

    public void setCheckSendP12(float checkSendP12) {
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
}
