package vn.easyca.signserver.webapp.service.model.generator;

public class CertGeneratorInput {

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
            result.CN = cn;
            result.OU = ou;
            result.O = o;
            result.L = l;
            result.S = s;
            result.C = c;
            return this;
        }

        public CertGeneratorInputBuilder setOwner(String ownerId, String ownerPhone, String ownerEmail) {
            result.ownerId = ownerId;
            result.ownerEmail = ownerEmail;
            result.ownerPhone = ownerPhone;
            return this;
        }

        public CertGeneratorInputBuilder setCertService(String certProfile, int certType) {
            result.certType = certType;
            result.certProfile = certProfile;
            return this;
        }


        public CertGeneratorInput build() {
            return result;
        }
    }

    private String alias;
    private int keyLength;
    private String CN;
    private String OU;
    private String O;
    private String L;
    private String S;
    private String C;
    private String ownerId;
    private String ownerPhone;
    private String ownerEmail;
    private int certType;
    private String certProfile;


    private CertGeneratorInput() {
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getAlias() {
        return alias;
    }

    public int getKeyLength() {
        return keyLength;
    }

    public String getCN() {
        return CN;
    }

    public String getOU() {
        return OU;
    }

    public String getO() {
        return O;
    }

    public String getL() {
        return L;
    }

    public String getS() {
        return S;
    }

    public String getC() {
        return C;
    }

    public String getCertProfile() {
        return certProfile;
    }

    public int getCertType() {
        return certType;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }
}
