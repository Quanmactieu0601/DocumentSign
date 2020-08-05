package vn.easyca.signserver.pki.cryptotoken;

import java.io.InputStream;

public class Config {
    private String name = "";
    private String library = "";
    private String slot = "";
    private String attributes = "";
    private String modulePin = "";
    private InputStream p12InputStream = null;

    private String pkcs11Config = "";

    public Config initPkcs11(String name, String library, String modulePin) {
        this.name = name;
        this.library = library;
        this.modulePin = modulePin;
        this.pkcs11Config = "name = " + this.name + "\n" + "library = " + this.library;
        return this;
    }

    public Config initPkcs12(InputStream is, String modulePin) {
        this.p12InputStream = is;
        this.modulePin = modulePin;
        return this;
    }

    public Config withSlot(String slot) {
        this.slot = slot;
        this.pkcs11Config = this.pkcs11Config + "\n slot = " + this.slot;
        return this;
    }

    public Config withAttributes(String attributes) {
        this.attributes = attributes;
        this.pkcs11Config = this.pkcs11Config + "\n attributes = " + this.attributes;
        return this;
    }

    public static Config build() {
        return new Config();
    }

    public String getPkcs11Config() {
        return pkcs11Config;
    }

    public String getModulePin() {
        return modulePin;
    }

    public InputStream getP12InputStream() {
        return p12InputStream;
    }

    public String getLibrary() {
        return library;
    }

    public String getSlot() {
        return slot;
    }

    public String getName() {
        return name;
    }
}
