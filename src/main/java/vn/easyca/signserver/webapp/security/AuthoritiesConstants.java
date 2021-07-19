package vn.easyca.signserver.webapp.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String SUPER_ADMIN = "ROLE_SUPER_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String CUSTOMER = "ROLE_CUSTOMER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static final String SIGN = "ROLE_SIGN";

    public static final String VERIFY = "ROLE_VERIFY";

    private AuthoritiesConstants() {
    }
}
