package core.dto;

public class CertDTO {

    private Long userId;
    private String ownerId;
    private String csr;
    private String cert;
    private String serial;
    private String message;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getCsr() {
        return csr;
    }

    public void setCsr(String csr) {
        this.csr = csr;
    }

    public String getCert() {
        return cert;
    }

    public void setCert(String cert) {
        this.cert = cert;
    }

    public CertDTO() {}

    public String getMessage() {
        return message == null ? "" : message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public CertDTO(Long userId, String ownerId, String message) {
        this.userId = userId;
        this.ownerId = ownerId;
        this.message = message;
    }

    public CertDTO(Long userId, String ownerId, String csr, String cert) {
        this.userId = userId;
        this.ownerId = ownerId;
        this.csr = csr;
        this.cert = cert;
    }
}
