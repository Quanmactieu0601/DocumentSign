package vn.easyca.signserver.core.domain;

public class SubjectDN {

    private final String cn;
    private final String ou;
    private final String o;
    private final String l;
    private final String s;
    private final String c;

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
        return String.format("CN=%s,", cn) +
            String.format("OU=%s,", ou) +
            String.format("O=%s,", o) +
            String.format("L=%s,", l) +
//        principalBuilder.append(String.format("S=%s,", s));
            String.format("C=%s", c);
    }
}
