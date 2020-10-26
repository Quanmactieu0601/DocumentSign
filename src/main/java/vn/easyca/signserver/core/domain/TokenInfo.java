package vn.easyca.signserver.core.domain;

import com.google.gson.Gson;

public class TokenInfo {

    private String name;

    private String library;

    private Long slot;

    private String password;

    private String data;

    private String p11Attrs;

    public static TokenInfo createInstance(String data) {
        Gson gson = new Gson();
        return gson.fromJson(data, TokenInfo.class);
    }

    public String getName() {
        return name;
    }

    public TokenInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getLibrary() {
        return library;
    }

    public TokenInfo setLibrary(String library) {
        this.library = library;
        return this;
    }

    public Long getSlot() {
        return slot;
    }

    public TokenInfo setSlot(Long slot) {
        this.slot = slot;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public TokenInfo setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getData() {
        return data;
    }

    public TokenInfo setData(String data) {
        this.data = data;
        return this;
    }

    public String getP11Attrs() {
        return p11Attrs;
    }

    public TokenInfo setP11Attrs(String p11Attrs) {
        this.p11Attrs = p11Attrs;
        return this;
    }

    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
