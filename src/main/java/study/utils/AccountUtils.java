package study.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class AccountUtils {

    public static String getLoggedAccount() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getUsername();
    }
}
