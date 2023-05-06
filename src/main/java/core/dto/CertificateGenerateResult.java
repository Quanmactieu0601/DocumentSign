package core.dto;

public class CertificateGenerateResult {

    private Cert cert;
    private User user;
    private String csr;

    public void setCert(Cert cert) {
        this.cert = cert;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Cert getCert() {
        return cert;
    }

    public User getUser() {
        return user;
    }

    public static class Cert {

        private String certData;
        private String certSerial;

        public Cert(String certSerial, String certData) {
            this.certSerial = certSerial;
            this.certData = certData;
        }

        public String getCertData() {
            return certData;
        }

        public String getCertSerial() {
            return certSerial;
        }
    }

    public static class User {

        private String username;
        private String userPassword;
        private String state;

        public User(String username, String userPassword, boolean isSuccess) {
            this.username = username;
            this.userPassword = isSuccess ? userPassword : "";
            this.state = isSuccess ? "OK" : "EXISTED";
        }

        public String getUsername() {
            return username;
        }

        public String getUserPassword() {
            return userPassword;
        }

        public String getState() {
            return state;
        }
    }

    public String getCsr() {
        return csr;
    }

    public void setCsr(String csr) {
        this.csr = csr;
    }
}
