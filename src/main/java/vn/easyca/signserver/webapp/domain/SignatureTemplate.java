package vn.easyca.signserver.webapp.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;

/**
 * A SignatureTemplate.
 */
@Entity
@Table(name = "signature_template")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SignatureTemplate extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "signature_image")
    private String signatureImage;

    @Column(name = "user_id")
    private Long userId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSignatureImage() {
        return signatureImage;
    }

    public SignatureTemplate signatureImage(String signatureImage) {
        this.signatureImage = signatureImage;
        return this;
    }

    public void setSignatureImage(String signatureImage) {
        this.signatureImage = signatureImage;
    }

    public Long getUserId() {
        return userId;
    }

    public SignatureTemplate userId(Long userId) {
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
        if (!(o instanceof SignatureTemplate)) {
            return false;
        }
        return id != null && id.equals(((SignatureTemplate) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SignatureTemplate{" +
            "id=" + getId() +
            ", signatureImage='" + getSignatureImage() + "'" +
            ", userId=" + getUserId() +
            "}";
    }
}
