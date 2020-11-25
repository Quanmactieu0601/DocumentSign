package vn.easyca.signserver.webapp.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.time.Instant;

/**
 * A Certificate.
 */
@Entity
@Table(name = "certificate")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Certificate implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String PKCS_11 = "PKCS_11";
    public static final String PKCS_12 = "PKCS_12";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "last_update")
    private String lastUpdate;

    @Column(name = "token_type")
    private String tokenType;

    @Column(name = "serial")
    private String serial;

    @Column(name = "owner_Id")
    private String ownerId;

    @Column(name = "subject_info")
    private String subjectInfo;

    @Column(name = "alias")
    private String alias;

    @Column(name = "token_info")
    private String tokenInfo;

    @Column(name = "raw_data")
    private String rawData;

    @Column(name = "valid_date")
    private Instant validDate;

    @Column(name = "expired_date")
    private Instant expiredDate;

    @Column(name = "active_status")
    private Integer activeStatus;

    @Column(name = "encrypted_pin")
    private String encryptedPin;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public Certificate lastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
        return this;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Certificate tokenType(String tokenType) {
        this.tokenType = tokenType;
        return this;
    }

    public Certificate validDate(Instant validDate){
        this.validDate = validDate;
        return this;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getSerial() {
        return serial;
    }

    public Certificate serial(String serial) {
        this.serial = serial;
        return this;
    }


    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public Certificate setOwnerId(String ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public String getSubjectInfo() {
        return subjectInfo;
    }

    public Certificate subjectInfo(String subjectInfo) {
        this.subjectInfo = subjectInfo;
        return this;
    }

    public void setSubjectInfo(String subjectInfo) {
        this.subjectInfo = subjectInfo;
    }

    public String getAlias() {
        return alias;
    }

    public Certificate alias(String alias) {
        this.alias = alias;
        return this;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getTokenInfo() {
        return tokenInfo;
    }

    public Certificate tokenInfo(String tokenInfo) {
        this.tokenInfo = tokenInfo;
        return this;
    }

    public void setTokenInfo(String tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    public String getRawData() {
        return rawData;
    }

    public Certificate rawData(String rawData) {
        this.rawData = rawData;
        return this;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Certificate)) {
            return false;
        }
        return id != null && id.equals(((Certificate) o).id);
    }

    public void setValidDate(Instant validDate) {
        this.validDate = validDate;
    }

    public Instant getValidDate() {
        return validDate;
    }


    public Instant getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Instant expiredDate) {
        this.expiredDate = expiredDate;
    }

    public int getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(Integer activeStatus) {
        this.activeStatus = activeStatus;
    }

    public String getEncryptedPin() {
        return encryptedPin;
    }

    public void setEncryptedPin(String encryptedPin) {
        this.encryptedPin = encryptedPin;
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "CertificateEntity{" +
            "id=" + id +
            ", lastUpdate='" + lastUpdate + '\'' +
            ", tokenType='" + tokenType + '\'' +
            ", serial='" + serial + '\'' +
            ", ownerId='" + ownerId + '\'' +
            ", subjectInfo='" + subjectInfo + '\'' +
            ", alias='" + alias + '\'' +
            ", tokenInfo='" + tokenInfo + '\'' +
            ", rawData='" + rawData + '\'' +
            ", validDate=" + validDate +
            ", expiredDate=" + expiredDate +
            ", activeStatus=" + activeStatus +
            '}';
    }
}
