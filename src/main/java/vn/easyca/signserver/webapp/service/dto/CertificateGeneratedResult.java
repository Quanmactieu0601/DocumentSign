package vn.easyca.signserver.webapp.service.dto;

public class CertificateGeneratedResult {

    private boolean successFully;
    private NewAccount newAccount;
    private NewCertificateInfo newCertificateInfo;

    public CertificateGeneratedResult(boolean successFully) {
        this.successFully = successFully;
    }

    public NewAccount getNewAccount() {
        return newAccount;
    }

    public NewCertificateInfo getNewCertificateInfo() {
        return newCertificateInfo;
    }

    public boolean isSuccessFully() {
        return successFully;
    }

    public CertificateGeneratedResult setNewAccount(NewAccount newAccount) {
        this.newAccount = newAccount;
        return this;
    }

    public CertificateGeneratedResult setNewCertificateInfo(NewCertificateInfo newCertificateInfo) {
        this.newCertificateInfo = newCertificateInfo;
        return this;
    }

}
