package vn.easyca.signserver.webapp.service.model.cert.data;

public class CertGeneratorInput {

    private String alias;
    private int keyLength;
    private SubjectInfo certProfile;
    private OwnerInfo ownerInfo;
    private ServiceInfo serviceInfo;

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

    public SubjectInfo getCertProfile() {
        return certProfile;
    }

    public ServiceInfo getServiceInfo() {
        return serviceInfo;
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
            SubjectInfo certProfile = new SubjectInfo(cn, ou, o, l, s, c);
            result.certProfile = certProfile;
            return this;
        }

        public CertGeneratorInputBuilder setOwner(String ownerId, String ownerPhone, String ownerEmail) {
            OwnerInfo ownerInfo = new OwnerInfo(ownerId, ownerEmail, ownerPhone);
            result.ownerInfo = ownerInfo;
            return this;
        }

        public CertGeneratorInputBuilder setCertService(String certProfile, int certProfileType, String certMethod) {
            ServiceInfo serviceInfo = new ServiceInfo(certMethod, certProfile, certProfileType);
            result.serviceInfo = serviceInfo;
            return this;
        }

        public CertGeneratorInput build() {
            return result;
        }
    }

}
