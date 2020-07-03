package vn.easyca.signserver.core.sign.integrated.pdf;

import vn.easyca.signserver.core.sign.cache.AbstractCachedObject;

import java.security.cert.Certificate;
import java.util.Date;

/**
 * Created by chen on 7/25/17.
 */
public class SignPDFCachedObject extends AbstractCachedObject {
    private String tmpFile;
    private Date signDate;
    private byte[] hash;
    private Certificate[] chain;

    public String getTmpFile() {
        return tmpFile;
    }

    public Date getSignDate() {
        return signDate;
    }

    public byte[] getHash() {
        return hash;
    }

    public Certificate[] getChain() {
        return chain;
    }

    public SignPDFCachedObject(String tmpFile, Date signDate, byte[] hash, Certificate[] chain) {
        this.tmpFile = tmpFile;
        this.signDate = signDate;
        this.hash = hash;
        this.chain = chain;
    }

    @Override
    public String getSigningType() {
        return "PDF";
    }
}
