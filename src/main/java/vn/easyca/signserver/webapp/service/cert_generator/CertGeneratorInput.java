package vn.easyca.signserver.webapp.service.cert_generator;

public class CertGeneratorInput {

    private String alias;
    private int keyLength;
    private SubjectDN subjectDN;
    private OwnerInfo ownerInfo;
    private CertPackage certPackage;

    private CertGeneratorInput() {
    }

    public OwnerInfo getOwnerInfo() {
        return ownerInfo;
    }

    public String getAlias() {
        return alias;
    }

    public int getKeyLength() {
        return keyLength;
    }

    public SubjectDN getSubjectDN() {
        return subjectDN;
    }

    public CertPackage getCertPackage() {
        return certPackage;
    }

    public static class CertGeneratorInputBuilder {

        private CertGeneratorInput result = new CertGeneratorInput();

        public CertGeneratorInputBuilder setAlias(String alias) {
            result.alias = alias;
            return this;
        }

        public CertGeneratorInputBuilder setKeyLength(int keyLength) {
            result.keyLength = keyLength;
            return this;
        }

        public CertGeneratorInputBuilder setAttrs(String cn, String ou, String o, String l, String s, String c) {
            SubjectDN subjectDN = new SubjectDN(cn, ou, o, l, s, c);
            result.subjectDN = subjectDN;
            return this;
        }

        public CertGeneratorInputBuilder setOwner(String ownerId, String ownerPhone, String ownerEmail) {
            OwnerInfo ownerInfo = new OwnerInfo(ownerId, ownerEmail, ownerPhone);
            result.ownerInfo = ownerInfo;
            return this;
        }

        public CertGeneratorInputBuilder setCertService(String certProfile, int certProfileType, String certMethod) {
            CertPackage certPackage = new CertPackage(certMethod, certProfile, certProfileType);
            result.certPackage = certPackage;
            return this;
        }

        public CertGeneratorInput build() {
            return result;
        }
    }

}
