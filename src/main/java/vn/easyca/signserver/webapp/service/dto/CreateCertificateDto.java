package vn.easyca.signserver.webapp.service.dto;

import java.util.Date;

public class CreateCertificateDto {

    private String ou;
    private String l;
    private String s;
    private String c;
    private String cn;
    private String password;
    private String ownerId;
    private Date fromDate;
    private Date toDate;
    private int keyLen;


    public String getOu() {
        return ou;
    }

    public CreateCertificateDto setOu(String ou) {
        this.ou = ou;
        return this;
    }

    public String getL() {
        return l;
    }

    public CreateCertificateDto setL(String l) {
        this.l = l;
        return this;
    }

    public String getS() {
        return s;
    }

    public CreateCertificateDto setS(String s) {
        this.s = s;
        return this;
    }

    public String getC() {
        return c;
    }

    public CreateCertificateDto setC(String c) {
        this.c = c;
        return this;
    }

    public String getCn() {
        return cn;
    }

    public CreateCertificateDto setCn(String cn) {
        this.cn = cn;
        return this;
    }

    public String getPassword() {
        return password;

    }

    public CreateCertificateDto setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public CreateCertificateDto setOwnerId(String ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public CreateCertificateDto setFromDate(Date fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    public Date getToDate() {
        return toDate;
    }

    public CreateCertificateDto setToDate(Date toDate) {
        this.toDate = toDate;
        return this;
    }

    public int getKeyLen() {
        return keyLen;
    }
}
