package vn.easyca.signserver.core.services.dto;

public class CertificateGeneratedResult {

    private Cert cert;
    private User user;

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

        private final String certData;
        private final String certSerial;

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
        private final String username;
        private final String password;
        private final int state;

        public User(String username, String password, int state) {
            this.username = username;
            this.password = password;
            this.state = state;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public int getState() {
            return state;
        }
    }

}
