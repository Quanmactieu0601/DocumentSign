package vn.easyca.signserver.webapp.service.cert_generator;

public class CertPackage {

    private String certMethod;
    private String certProfile;
    private int certProfileType;

    public CertPackage(String certMethod, String certProfile, int certProfileType) {
        this.certMethod = certMethod;
        this.certProfile = certProfile;
        this.certProfileType = certProfileType;
    }

    public String getCertMethod() {
        return certMethod;
    }

    public String getCertProfile() {
        return certProfile;
    }

    public int getCertProfileType() {
        return certProfileType;
    }
}
