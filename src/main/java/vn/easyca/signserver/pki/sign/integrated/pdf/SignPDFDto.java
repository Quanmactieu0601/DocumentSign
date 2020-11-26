package vn.easyca.signserver.pki.sign.integrated.pdf;

import vn.easyca.signserver.pki.sign.utils.StringUtils;
import vn.easyca.signserver.webapp.config.Constants;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Date;

public class SignPDFDto {

    private PartyMode partyMode;
    private String hashAlg;
    private byte[] content;
    private PrivateKey key;
    private Certificate[] chain;
    private String location;
    private String reason = "";
    private String signerLabel = "Ký bởi";
    private String signer = "";
    private String signDateLabel = "Ký ngày";
    private int pageNumber = 1;
    private int visibleX = 0;
    private int visibleY = 0;
    private int visibleWidth = 150;
    private int visibleHeight = 100;
    private String signField;
    private Date signDate;
    private String outPath;

    private static SignPDFDto instance;

    public void setPartyMode(PartyMode partyMode) {
        this.partyMode = partyMode;
    }

    public void setHashAlg(String hashAlg) {
        this.hashAlg = hashAlg;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public void setKey(PrivateKey key) {
        this.key = key;
    }

    public void setChain(Certificate[] chain) {
        this.chain = chain;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setSignerLabel(String signerLabel) {
        this.signerLabel = signerLabel;
    }

    public void setSignDateLabel(String signDateLabel) {
        this.signDateLabel = signDateLabel;
    }

    public void setSigner(String signer) {
        this.signer = signer;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setVisibleX(int visibleX) {
        this.visibleX = visibleX;
    }

    public void setVisibleY(int visibleY) {
        this.visibleY = visibleY;
    }

    public void setVisibleWidth(int visibleWidth) {
        this.visibleWidth = visibleWidth;
    }

    public void setVisibleHeight(int visibleHeight) {
        this.visibleHeight = visibleHeight;
    }

    public void setSignField(String signField) {
        this.signField = signField;
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }

    public void setOutPath(String outPath) {
        this.outPath = outPath;
    }

    public PartyMode getPartyMode() {
        return partyMode;
    }

    public String getHashAlg() {
        return hashAlg;
    }

    public byte[] getContent() {
        return content;
    }

    public PrivateKey getKey() {
        return key;
    }

    public Certificate[] getChain() {
        return chain;
    }

    public Certificate getFirstCert() {
        return getChain()[0];
    }

    public String getLocation() {
        return location;
    }

    public String getReason() {
        return reason;
    }

    public String getSignerLabel() {
        return signerLabel;
    }

    public String getSigner() {
        return signer;
    }

    public String getSignDateLabel() {
        return signDateLabel;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getVisibleX() {
        return visibleX;
    }

    public int getVisibleY() {
        return visibleY;
    }

    public int getVisibleWidth() {
        return visibleWidth;
    }

    public int getVisibleHeight() {
        return visibleHeight;
    }

    public String getSignField() {
        return signField;
    }

    public Date getSignDate() {
        return signDate == null ? new Date() : signDate;
    }

    public String getOutPath() {
        return outPath;
    }

    private SignPDFDto() {
    }

    public static SignPDFDto build(PartyMode mode, byte[] content, PrivateKey key, Certificate[] chain, String outPath) {
        instance = new SignPDFDto();
        instance.setPartyMode(mode);
        instance.setContent(content);
        instance.setKey(key);
        instance.setChain(chain);
        instance.setOutPath(outPath);
        return instance;
    }

    /**
     * public void sign(byte[] content, PrivateKey key, Certificate[] chain, JSONObject signatureInfo, JSONObject signatureVisible,
     * String signField, String hashAlg, Date signDate, String outPath) throws Exception {
     */

    public SignPDFDto withHashAlg(String hashAlg) {
        instance.setHashAlg(StringUtils.isNullOrEmpty(hashAlg) ? Constants.HASH_ALGORITHM.SHA1 : hashAlg);
        return instance;
    }

    public SignPDFDto withLocation(String location) {
        instance.setLocation(location);
        return instance;
    }

    public SignPDFDto withReason(String reason) {
        instance.setReason(reason);
        return instance;
    }

    public SignPDFDto withSignerLabel(String signerLabel) {
        instance.setSignerLabel(signerLabel);
        return instance;
    }

    public SignPDFDto withSigner(String signer) {
        instance.setSigner(signer);
        return instance;
    }

    public SignPDFDto withSignDateLabel(String signDateLabel) {
        instance.setSignDateLabel(signDateLabel);
        return instance;
    }

    public SignPDFDto withPageNumber(int pageNumber) {
        instance.setPageNumber(pageNumber);
        return instance;
    }

    public SignPDFDto withVisibleX(int visibleX) {
        instance.setVisibleX(visibleX);
        return instance;
    }

    public SignPDFDto withVisibleY(int visibleY) {
        instance.setVisibleY(visibleY);
        return instance;
    }

    public SignPDFDto withVisibleWidth(int visibleWidth) {
        instance.setVisibleWidth(visibleWidth);
        return instance;
    }

    public SignPDFDto withVisibleHeight(int visibleHeight) {
        instance.setVisibleHeight(visibleHeight);
        return instance;
    }

    public SignPDFDto withSignField(String signField) {
        instance.setSignField(signField);
        return instance;
    }

    public SignPDFDto withSignDate(Date signDate) {
        instance.setSignDate(signDate);
        return instance;
    }
}
