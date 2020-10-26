package vn.easyca.signserver.core.interfaces;

public interface UserCreator {

    public class UserCreatorException extends  Exception{

        public UserCreatorException(String message) {
            super(message);
        }

        public UserCreatorException(Throwable cause) {
            super(cause);
        }

        public UserCreatorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

    int RESULT_CREATED = 1;
    int RESULT_EXIST = 2;
    int CreateUser(String username, String password, String fullName) throws UserCreatorException;
}
