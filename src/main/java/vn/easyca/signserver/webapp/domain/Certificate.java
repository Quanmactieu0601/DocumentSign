package vn.easyca.signserver.webapp.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;

/**
 * A Certificate.
 */
@Entity
@Table(name = "certificate")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Certificate implements Serializable {

    private static final long serialVersionUID = 1L;

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

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Certificate{" +
            "id=" + getId() +
            ", lastUpdate='" + getLastUpdate() + "'" +
            ", tokenType='" + getTokenType() + "'" +
            ", serial='" + getSerial() + "'" +
            ", ownerId='" + getOwnerId() + "'" +
            ", subjectInfo='" + getSubjectInfo() + "'" +
            ", alias='" + getAlias() + "'" +
            ", tokenInfo='" + getTokenInfo() + "'" +
            ", rawData='" + getRawData() + "'" +
            "}";
    }

    @Transient private X509Certificate x509Certificate;

    public X509Certificate getX509Certificate() throws CertificateException {
        if (x509Certificate != null)
            return x509Certificate;
        byte encodedCert[] = Base64.getDecoder().decode(rawData);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(encodedCert);
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        return (X509Certificate) certFactory.generateCertificate(inputStream);
    }

    public Date getValidFromDate() throws CertificateException, KeyStoreException {
        return getX509Certificate().getNotBefore();
    }

    public Date getValidToDate() throws CertificateException, KeyStoreException {
        return getX509Certificate().getNotAfter();
    }

    public TokenInfo getCertificateTokenInfo() {
        return TokenInfo.createInstance(tokenInfo);
    }

    public void setCertificateTokenInfo(TokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo.toString();
    }

    public CertificateType getCertificateType() {
        return CertificateType.getType(tokenType);
    }

    public boolean isExtensionCert(Certificate oldCert) {

        try {
            return serial != null && serial.contentEquals(oldCert.getSerial()) &&
                   oldCert.getValidToDate().before(this.getValidFromDate());
        } catch (KeyStoreException | CertificateException e) {
            return false;
        }

    }


}
