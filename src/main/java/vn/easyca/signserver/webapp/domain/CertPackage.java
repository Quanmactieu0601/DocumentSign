package vn.easyca.signserver.webapp.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A CertPackage.
 */
@Entity
@Table(name = "cert_package")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CertPackage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "package_code")
    private String packageCode;

    @Column(name = "cert_type")
    private Integer certType;

    @Column(name = "name_cert")
    private String nameCert;

    @Column(name = "key_length")
    private Integer keyLength;

    @Column(name = "expired_date")
    private LocalDate expiredDate;

    @Column(name = "signing_turn")
    private Integer signingTurn;

    @Column(name = "price", precision = 21, scale = 2)
    private BigDecimal price;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public CertPackage packageCode(String packageCode) {
        this.packageCode = packageCode;
        return this;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getCertType() {
        return certType;
    }

    public CertPackage certType(Integer certType) {
        this.certType = certType;
        return this;
    }

    public void setCertType(Integer certType) {
        this.certType = certType;
    }

    public String getNameCert() {
        return nameCert;
    }

    public CertPackage nameCert(String nameCert) {
        this.nameCert = nameCert;
        return this;
    }

    public void setNameCert(String nameCert) {
        this.nameCert = nameCert;
    }

    public Integer getKeyLength() {
        return keyLength;
    }

    public CertPackage keyLength(Integer keyLength) {
        this.keyLength = keyLength;
        return this;
    }

    public void setKeyLength(Integer keyLength) {
        this.keyLength = keyLength;
    }

    public LocalDate getExpiredDate() {
        return expiredDate;
    }

    public CertPackage expiredDate(LocalDate expiredDate) {
        this.expiredDate = expiredDate;
        return this;
    }

    public void setExpiredDate(LocalDate expiredDate) {
        this.expiredDate = expiredDate;
    }

    public Integer getSigningTurn() {
        return signingTurn;
    }

    public CertPackage signingTurn(Integer signingTurn) {
        this.signingTurn = signingTurn;
        return this;
    }

    public void setSigningTurn(Integer signingTurn) {
        this.signingTurn = signingTurn;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public CertPackage price(BigDecimal price) {
        this.price = price;
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CertPackage)) {
            return false;
        }
        return id != null && id.equals(((CertPackage) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CertPackage{" +
            "id=" + getId() +
            ", packageCode='" + getPackageCode() + "'" +
            ", certType=" + getCertType() +
            ", nameCert='" + getNameCert() + "'" +
            ", keyLength=" + getKeyLength() +
            ", expiredDate='" + getExpiredDate() + "'" +
            ", signingTurn=" + getSigningTurn() +
            ", price=" + getPrice() +
            "}";
    }
}
