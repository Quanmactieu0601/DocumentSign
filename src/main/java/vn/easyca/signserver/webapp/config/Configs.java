package vn.easyca.signserver.webapp.config;

import vn.easyca.signserver.core.cryptotoken.Config;

public class Configs {

    private static final String CA_URL = "http://172.16.10.66:8787/api/";
    private static final String CA_USER = "admin";
    private static final String CA_PASS = "admin";

    private static final String TOKEN_NAME = "";
    private static final String TOKEN_LIB = "";
    private static final String TOKEN_PIN = "";

    public static Config getCryptoConfigForGenCert() {
        Config cfg = new Config();
        cfg.initPkcs11(TOKEN_NAME, TOKEN_LIB, TOKEN_PIN);
        return cfg;
    }

    public static CAConfig getCAConfig() {
        return new CAConfig(CA_URL, CA_USER, CA_PASS);
    }


    public static class CAConfig {
        private String url;
        private String username;
        private String password;

        private CAConfig(String url, String username, String password) {
            this.url = url;
            this.username = username;
            this.password = password;
        }

        public String getUrl() {
            return url;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

}
