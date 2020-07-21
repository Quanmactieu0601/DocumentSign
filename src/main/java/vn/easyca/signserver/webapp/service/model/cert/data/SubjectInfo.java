package vn.easyca.signserver.webapp.service.model.cert.data;

public class SubjectInfo {

    private String cn;
    private String ou;
    private String o;
    private String l;
    private String s;
    private String c;

    public SubjectInfo(String cn, String ou, String o, String l, String s, String c) {
        this.cn = cn;
        this.ou = ou;
        this.o = o;
        this.l = l;
        this.s = s;
        this.c = c;
    }

    public String getCn() {
        return cn;
    }

    public String getOu() {
        return ou;
    }

    public String getO() {
        return o;
    }

    public String getL() {
        return l;
    }

    public String getS() {
        return s;
    }

    public String getC() {
        return c;
    }
}
