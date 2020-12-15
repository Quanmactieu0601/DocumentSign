package vn.easyca.signserver.webapp;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.config.ApplicationProperties;

import io.github.jhipster.config.DefaultProfileUtil;
import io.github.jhipster.config.JHipsterConstants;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import vn.easyca.signserver.webapp.service.SystemConfigCachingService;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

@ComponentScan({"vn.easyca.signserver", "vn.easyca.signserver.core.services"})
@EntityScan({"vn.easyca.signserver.infrastructure.database.jpa.entity", "vn.easyca.signserver.webapp.domain"})
@SpringBootApplication
@EnableConfigurationProperties({LiquibaseProperties.class, ApplicationProperties.class})
@EnableJpaRepositories(basePackages = {"vn.easyca.signserver.webapp.repository"})
public class WebappApp {
    private static final Logger log = LoggerFactory.getLogger(WebappApp.class);

    private final Environment env;
    private static SystemConfigCachingService systemConfigCachingService = null;

    public WebappApp(Environment env, SystemConfigCachingService systemConfigCachingService) {
        this.env = env;
        this.systemConfigCachingService = systemConfigCachingService;
    }

    /**
     * Initializes webapp.
     * <p>
     * Spring profiles can be configured with a program argument --spring.profiles.active=your-active-profile
     * <p>
     * You can find more information on how profiles work with JHipster on <a href="https://www.jhipster.tech/profiles/">https://www.jhipster.tech/profiles/</a>.
     */
    @PostConstruct
    public void initApplication() {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_PRODUCTION)) {
            log.error("You have miss configured your application! It should not run " +
                "with both the 'dev' and 'prod' profiles at the same time.");
        }
        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_CLOUD)) {
            log.error("You have miss configured your application! It should not " +
                "run with both the 'dev' and 'cloud' profiles at the same time.");
        }
    }

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(WebappApp.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
        init();
    }

    private static void init() {
        // init ra-service
        // TODO: Refactor this code
//        RAConfig raConfig = new RAConfig(Constants.RAConfig.URL, Constants.RAConfig.UserName, Constants.RAConfig.Password);
//        CertificateRequesterImpl.init(new RAServiceFade(raConfig));


        // init system config cache
        try {
            systemConfigCachingService.getConfig();
        } catch (ApplicationException e) {
            log.error(e.getMessage());
        }
    }

    private static void logApplicationStartup(Environment env) {
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (StringUtils.isBlank(contextPath)) {
            contextPath = "/";
        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        log.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\t{}://localhost:{}{}\n\t" +
                "External: \t{}://{}:{}{}\n\t" +
                "Profile(s): \t{}\n----------------------------------------------------------",
            env.getProperty("spring.application.name"),
            protocol,
            serverPort,
            contextPath,
            protocol,
            hostAddress,
            serverPort,
            contextPath,
            env.getActiveProfiles());
    }
}
