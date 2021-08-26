package vn.easyca.signserver.webapp.service.dto;

import java.time.LocalDate;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A DTO for the {@link vn.easyca.signserver.webapp.domain.CertPackage} entity.
 */
public class CertPackageDTO implements Serializable {
    
    private Long id;

    private String packageCode;

    private Integer certType;

    private String nameCert;

    private Integer keyLength;

    private LocalDate expiredDate;

    private Integer signingTurn;

    private BigDecimal price;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getCertType() {
        return certType;
    }

    public void setCertType(Integer certType) {
        this.certType = certType;
    }

    public String getNameCert() {
        return nameCert;
    }

    public void setNameCert(String nameCert) {
        this.nameCert = nameCert;
    }

    public Integer getKeyLength() {
        return keyLength;
    }

    public void setKeyLength(Integer keyLength) {
        this.keyLength = keyLength;
    }

    public LocalDate getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(LocalDate expiredDate) {
        this.expiredDate = expiredDate;
    }

    public Integer getSigningTurn() {
        return signingTurn;
    }

    public void setSigningTurn(Integer signingTurn) {
        this.signingTurn = signingTurn;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CertPackageDTO)) {
            return false;
        }

        return id != null && id.equals(((CertPackageDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CertPackageDTO{" +
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
