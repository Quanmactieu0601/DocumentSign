package vn.easyca.signserver.business.domain;

public class SubjectDN {

    private String cn;
    private String ou;
    private String o;
    private String l;
    private String s;
    private String c;

    public SubjectDN(String cn, String ou, String o, String l, String s, String c) {
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

    public String toString() {
        StringBuilder principalBuilder = new StringBuilder();
        principalBuilder.append(String.format("CN=%s,", cn));
        principalBuilder.append(String.format("OU=%s,", ou));
        principalBuilder.append(String.format("O=%s,", o));
        principalBuilder.append(String.format("L=%s,", l));
//        principalBuilder.append(String.format("S=%s,", s));
        principalBuilder.append(String.format("C=%s", c));
        return principalBuilder.toString();
    }
}
