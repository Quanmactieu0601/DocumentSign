package vn.easyca.signserver.webapp.service.dto;

import java.io.Serializable;

/**
 * A DTO for the {@link vn.easyca.signserver.webapp.domain.SignatureImage} entity.
 */
public class SignatureImageDTO implements Serializable {
    
    private Long id;

    private String imgData;

    private Long userId;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImgData() {
        return imgData;
    }

    public void setImgData(String imgData) {
        this.imgData = imgData;
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
        if (!(o instanceof SignatureImageDTO)) {
            return false;
        }

        return id != null && id.equals(((SignatureImageDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SignatureImageDTO{" +
            "id=" + getId() +
            ", imgData='" + getImgData() + "'" +
            ", userId=" + getUserId() +
            "}";
    }
}
