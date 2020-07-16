package vn.easyca.signserver.webapp.service.dto;

public class NewAccount{

    private String userName;

    private String password;

    public NewAccount(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }
}
