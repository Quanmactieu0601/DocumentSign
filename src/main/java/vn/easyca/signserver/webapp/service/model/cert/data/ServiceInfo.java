package vn.easyca.signserver.webapp.service.model.cert.data;

public class ServiceInfo {

    private String certMethod;
    private String certProfile;
    private int certProfileType;

    public ServiceInfo(String certMethod, String certProfile, int certProfileType) {
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
