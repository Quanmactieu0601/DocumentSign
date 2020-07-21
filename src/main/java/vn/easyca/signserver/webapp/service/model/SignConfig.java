package vn.easyca.signserver.webapp.service.model;

public class SignConfig {


    public  enum HashAlgorithm {

        SHA1("SHA1"),
        SHA256("SHA256"),
        SHA512("SHA512");

        HashAlgorithm(String val) {
            this.val = val;
        }

        private String val;

        public String getVal() {
            return val;
        }

        public static HashAlgorithm getInstance(String val) {

            if (val != null)
                val = val.toUpperCase();
            if (val == SHA256.val)
                return SHA256;
            else if (val == SHA512.val)
                return SHA512;
            return SHA1;
        }
    }

    public  enum SignatureAlgorithm {

        SHA1WITHRSA("SHA1withRSA"),
        SHA256WITHRSA("SHA256withRSA");

        SignatureAlgorithm(String val) {
            this.val = val;
        }

        private String val;

        public String getVal() {
            return val;
        }

        public static SignatureAlgorithm getInstance(String val) {

            if (val != null)
                val = val.toUpperCase();
            if (val == SHA256WITHRSA.val)
                return SHA256WITHRSA;
            return SHA1WITHRSA;
        }
    }

    private HashAlgorithm hashAlgorithm = HashAlgorithm.SHA1;

    private SignatureAlgorithm signAlgorithm = SignatureAlgorithm.SHA1WITHRSA;

    public SignConfig(HashAlgorithm hashAlgorithm, SignatureAlgorithm signAlgorithm) {

        this.hashAlgorithm = hashAlgorithm;
        this.signAlgorithm = signAlgorithm;
    }

    public SignConfig() {
        this.hashAlgorithm = HashAlgorithm.SHA1;
        this.signAlgorithm = SignatureAlgorithm.SHA1WITHRSA;
    }

}
