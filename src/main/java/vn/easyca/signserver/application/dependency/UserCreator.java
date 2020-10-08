package vn.easyca.signserver.application.dependency;

public interface UserCreator {
    int RESULT_CREATED = 1;
    int RESULT_EXIST = 2;
    int CreateUser(String username, String password, String fullName) throws Exception;
}
