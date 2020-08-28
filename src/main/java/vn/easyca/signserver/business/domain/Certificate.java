package vn.easyca.signserver.business.domain;

import vn.easyca.signserver.business.utils.CommonUtils;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

public class Certificate {

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

    private X509Certificate x509Certificate;

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

    public X509Certificate getX509Certificate() throws CertificateException {

        if (x509Certificate != null)
            return x509Certificate;
        return x509Certificate = CommonUtils.decodeBase64X509(rawData);
    }
}
