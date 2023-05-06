package ra.lib;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:ra-config.properties")
public class RAConfig {

    @Value("${connect-to-ra}")
    private Boolean isConnectToRA;

    @Value("${ra-url}")
    private String baseUrl;

    @Value("${ra-account}")
    private String userName;

    @Value("${ra-password}")
    private String password;

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isConnectToRA() {
        return isConnectToRA == null ? false : isConnectToRA;
    }
}
