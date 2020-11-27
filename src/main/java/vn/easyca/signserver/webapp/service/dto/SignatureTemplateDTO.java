package vn.easyca.signserver.webapp.service.dto;

import java.io.Serializable;

/**
 * A DTO for the {@link vn.easyca.signserver.webapp.domain.SignatureTemplate} entity.
 */
public class SignatureTemplateDTO implements Serializable {
    
    private Long id;

    private String signatureImage;

    private Long userId;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSignatureImage() {
        return signatureImage;
    }

    public void setSignatureImage(String signatureImage) {
        this.signatureImage = signatureImage;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SignatureTemplateDTO)) {
            return false;
        }

        return id != null && id.equals(((SignatureTemplateDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SignatureTemplateDTO{" +
            "id=" + getId() +
            ", signatureImage='" + getSignatureImage() + "'" +
            ", userId=" + getUserId() +
            "}";
    }
}
