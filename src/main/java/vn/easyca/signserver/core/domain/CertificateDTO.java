package vn.easyca.signserver.core.domain;

import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.utils.CertUtils;

import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
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

    private LocalDateTime validDate;

    private LocalDateTime expiredDate;

    private int activeStatus;

    private String rawPin;

    private String encryptedPin;

    private X509Certificate x509Certificate;

    private Long signatureImageId;

    private String secretKey;

    private Integer signedTurnCount;

    public Long packageId;

    private String personalId;

    private String authMode;

    private int type;

    private int singingProfile;

    public String getPersonalId() { return personalId; }

    public void setPersonalId(String personalId) { this.personalId = personalId; }

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

    public LocalDateTime getValidDate() {
        return validDate;
    }

    public void setValidDate(LocalDateTime validDate) {
        this.validDate = validDate;
    }

    public LocalDateTime getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(LocalDateTime expiredDate) {
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

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public X509Certificate getX509Certificate() throws ApplicationException {
        if (x509Certificate != null)
            return x509Certificate;
        return x509Certificate = CertUtils.decodeBase64X509(rawData);
    }
    public Long getSignatureImageId() {
        return signatureImageId;
    }

    public void setSignatureImageId(Long signatureImageId) {
        this.signatureImageId = signatureImageId;
    }

    public String getRawPin() {
        return rawPin;
    }

    public void setRawPin(String rawPin) {
        this.rawPin = rawPin;
    }

    public Integer getSignedTurnCount() { return signedTurnCount; }

    public void setSignedTurnCount(Integer signedTurnCount) { this.signedTurnCount = signedTurnCount; }

    public Long getPackageId() { return packageId; }

    public void setPackageId(Long packageId) { this.packageId = packageId; }


    public String getAuthMode() {
        return authMode;
    }

    public void setAuthMode(String authMode) {
        this.authMode = authMode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSingingProfile() {
        return singingProfile;
    }

    public void setSingingProfile(int singingProfile) {
        this.singingProfile = singingProfile;
    }


}
