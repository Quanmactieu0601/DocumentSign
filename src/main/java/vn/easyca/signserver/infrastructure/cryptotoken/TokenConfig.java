package vn.easyca.signserver.infrastructure.cryptotoken;

import jdk.nashorn.internal.parser.Token;

public class TokenConfig {

    private String name = "EasyCAToken";
    private String lib = "C:\\Windows\\System32\\easyca_csp11_v1.dll";
    private String pin = "12345678";
    private String slot = null;
    private String attr = null;

    public String getAttr() {
        return attr;
    }

    public String getLib() {
        return lib;
    }

    public String getName() {
        return name;
    }

    public String getPin() {
        return pin;
    }

    public String getSlot() {
        return slot;
    }

    public boolean isInit(){
        return false;
    }

    private TokenConfig() {
    }

    private static TokenConfig instance;

    public static TokenConfig getInstance() {
        if (instance == null)
            instance = new TokenConfig();
        return instance;
    }

    public void Init(String name, String lib, String pin, String slot, String attr) {
        this.name = name;
        this.lib = lib;
        this.pin = pin;
        this.slot = slot;
        this.attr = attr;
    }


}
