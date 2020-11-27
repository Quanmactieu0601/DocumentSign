package vn.easyca.signserver.core.domain;

import vn.easyca.signserver.core.utils.CommonUtils;

import javax.persistence.Column;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Date;

public class CertificateDTO {

    public static final String PKCS_11 = "PKCS_11";

    public static final String PKCS_12 = "PKCS_12";

    private Long id;

    private Date modifiedDate;

    private String tokenType;

    private String serial;

    private String ownerId;

    private String subjectInfo;

    private String alias;

    private String rawData;

    private TokenInfo tokenInfo;

    private Instant validDate;

    private Instant expiredDate;

    private int activeStatus;

    private String encryptedPin;

    private X509Certificate x509Certificate;

    private Long signatureImageId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getSubjectInfo() {
        return subjectInfo;
    }

    public void setSubjectInfo(String subjectInfo) {
        this.subjectInfo = subjectInfo;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public TokenInfo getTokenInfo() {
        return tokenInfo;
    }

    public void setTokenInfo(TokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    public Instant getValidDate() {
        return validDate;
    }

    public void setValidDate(Instant validDate) {
        this.validDate = validDate;
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

    public void setActiveStatus(int active_status) {
        this.activeStatus = active_status;
    }

    public String getEncryptedPin() {
        return encryptedPin;
    }

    public void setEncryptedPin(String encryptedPin) {
        this.encryptedPin = encryptedPin;
    }

    public X509Certificate getX509Certificate() throws CertificateException {
        if (x509Certificate != null)
            return x509Certificate;
        return x509Certificate = CommonUtils.decodeBase64X509(rawData);
    }
    public Long getSignatureImageId() {
        return signatureImageId;
    }

    public void setSignatureImageId(Long signatureImageId) {
        this.signatureImageId = signatureImageId;
    }
}
