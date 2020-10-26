package vn.easyca.signserver.ra.lib;

public class RAConfig {
    private String baseUrl;
    private String userName;
    private String password;

    public RAConfig(String baseUrl, String userName, String password) {
        this.baseUrl = baseUrl;
        this.userName = userName;
        this.password = password;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
