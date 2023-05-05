package study.domain;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SignatureImage.
 */
@Entity
@Table(name = "signature_image")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SignatureImage extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "img_data")
    private String imgData;

    @Column(name = "user_id")
    private Long userId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImgData() {
        return imgData;
    }

    public SignatureImage imgData(String imgData) {
        this.imgData = imgData;
        return this;
    }

    public void setImgData(String imgData) {
        this.imgData = imgData;
    }

    public Long getUserId() {
        return userId;
    }

    public SignatureImage userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SignatureImage)) {
            return false;
        }
        return id != null && id.equals(((SignatureImage) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SignatureImage{" +
            "id=" + getId() +
            ", imgData='" + getImgData() + "'" +
            ", userId=" + getUserId() +
            "}";
    }
}
