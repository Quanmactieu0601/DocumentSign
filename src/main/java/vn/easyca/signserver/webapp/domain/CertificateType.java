package vn.easyca.signserver.webapp.domain;

public enum CertificateType {

    PKCS11("PKCS_11"), PKCS12("PKCS_12");
    public static final  String PKCS_12 ="PKCS_12";
    public static final  String PKCS_11 ="PKCS_11";

    private String val;

    CertificateType(String val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return val;
    }

    public static CertificateType getType(String val) {

        if (val != null)
            val = val.toUpperCase();
        if (val == PKCS11.val)
            return PKCS11;
        if (val == PKCS12.val)
            return PKCS12;
        return null;

    }
}
