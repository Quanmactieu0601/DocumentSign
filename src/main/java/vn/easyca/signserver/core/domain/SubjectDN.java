package vn.easyca.signserver.core.domain;

import org.apache.commons.lang3.StringUtils;

public class SubjectDN {

    private String cn; // common name;
    private String t; // title;
    private String ou; // organization unit;
    private String o; // organization;
    private String l; // locality;
    private String s; // state;
    private String c; // country;

    public SubjectDN(String cn, String t, String ou, String o, String l, String s, String c) {
        this.cn = cn;
        this.t = t;
        this.ou = ou;
        this.o = o;
        this.l = l;
        this.s = s;
        this.c = c;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
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

    public String getSubjectDN() {
        return toString();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        if(StringUtils.isNotEmpty(cn)) {
            if (cn.contains(","))
                cn = String.format("\"%s\"", cn);
            builder.append("CN=").append(cn);
        }
        if(StringUtils.isNotEmpty(t)) {
            builder.append(",T=").append(t);
        }
        if(StringUtils.isNotEmpty(ou)) {
            builder.append(",OU=").append(ou);
        }
        if(StringUtils.isNotEmpty(o)) {
            builder.append(",O=").append(o);
        }
        if(StringUtils.isNotEmpty(l)) {
            builder.append(",L=").append(l);
        }
        if(StringUtils.isNotEmpty(s)) {
            builder.append(",S=").append(s);
        }
        if(StringUtils.isNotEmpty(c)) {
            builder.append(",C=").append(c);
        }
        return builder.toString();
    }
}
