package vn.easyca.signserver.webapp.web.rest.vm.request;

public class CertificateChangeOwnVN {
    private String ownerId;
    private Long id;

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
