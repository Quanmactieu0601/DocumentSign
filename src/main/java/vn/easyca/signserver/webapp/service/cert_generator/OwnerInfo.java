package vn.easyca.signserver.webapp.service.cert_generator;

public class OwnerInfo {

    private String ownerId;
    private String ownerEmail;
    private String ownerPhone;

    public OwnerInfo(String ownerId, String ownerEmail, String ownerPhone) {
        this.ownerId = ownerId;
        this.ownerEmail = ownerEmail;
        this.ownerPhone = ownerPhone;
    }


    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
