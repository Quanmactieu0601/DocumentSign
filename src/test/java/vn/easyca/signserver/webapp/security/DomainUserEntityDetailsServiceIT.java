package vn.easyca.signserver.webapp.security;

import vn.easyca.signserver.webapp.WebappApp;
import vn.easyca.signserver.infrastructure.database.jpa.entity.UserEntity;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;
import vn.easyca.signserver.infrastructure.database.jpa.repository.UserRepository;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Integrations tests for {@link DomainUserDetailsService}.
 */
@SpringBootTest(classes = WebappApp.class)
@Transactional
public class DomainUserEntityDetailsServiceIT {

    private static final String USER_ONE_LOGIN = "test-user-one";
    private static final String USER_ONE_EMAIL = "test-user-one@localhost";
    private static final String USER_TWO_LOGIN = "test-user-two";
    private static final String USER_TWO_EMAIL = "test-user-two@localhost";
    private static final String USER_THREE_LOGIN = "test-user-three";
    private static final String USER_THREE_EMAIL = "test-user-three@localhost";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsService domainUserDetailsService;

    @BeforeEach
    public void init() {
        UserEntity userEntityOne = new UserEntity();
        userEntityOne.setLogin(USER_ONE_LOGIN);
        userEntityOne.setPassword(RandomStringUtils.random(60));
        userEntityOne.setActivated(true);
        userEntityOne.setEmail(USER_ONE_EMAIL);
        userEntityOne.setFirstName("userOne");
        userEntityOne.setLastName("doe");
        userEntityOne.setLangKey("en");
        userRepository.save(userEntityOne);

        UserEntity userEntityTwo = new UserEntity();
        userEntityTwo.setLogin(USER_TWO_LOGIN);
        userEntityTwo.setPassword(RandomStringUtils.random(60));
        userEntityTwo.setActivated(true);
        userEntityTwo.setEmail(USER_TWO_EMAIL);
        userEntityTwo.setFirstName("userTwo");
        userEntityTwo.setLastName("doe");
        userEntityTwo.setLangKey("en");
        userRepository.save(userEntityTwo);

        UserEntity userEntityThree = new UserEntity();
        userEntityThree.setLogin(USER_THREE_LOGIN);
        userEntityThree.setPassword(RandomStringUtils.random(60));
        userEntityThree.setActivated(false);
        userEntityThree.setEmail(USER_THREE_EMAIL);
        userEntityThree.setFirstName("userThree");
        userEntityThree.setLastName("doe");
        userEntityThree.setLangKey("en");
        userRepository.save(userEntityThree);
    }

    @Test
    public void assertThatUserCanBeFoundByLogin() {
        UserDetails userDetails = domainUserDetailsService.loadUserByUsername(USER_ONE_LOGIN);
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(USER_ONE_LOGIN);
    }

    @Test
    public void assertThatUserCanBeFoundByLoginIgnoreCase() {
        UserDetails userDetails = domainUserDetailsService.loadUserByUsername(USER_ONE_LOGIN.toUpperCase(Locale.ENGLISH));
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(USER_ONE_LOGIN);
    }

    @Test
    public void assertThatUserCanBeFoundByEmail() {
        UserDetails userDetails = domainUserDetailsService.loadUserByUsername(USER_TWO_EMAIL);
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(USER_TWO_LOGIN);
    }

    @Test
    public void assertThatUserCanBeFoundByEmailIgnoreCase() {
        UserDetails userDetails = domainUserDetailsService.loadUserByUsername(USER_TWO_EMAIL.toUpperCase(Locale.ENGLISH));
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(USER_TWO_LOGIN);
    }

    @Test
    public void assertThatEmailIsPrioritizedOverLogin() {
        UserDetails userDetails = domainUserDetailsService.loadUserByUsername(USER_ONE_EMAIL);
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(USER_ONE_LOGIN);
    }

    @Test
    public void assertThatUserNotActivatedExceptionIsThrownForNotActivatedUsers() {
        assertThatExceptionOfType(UserNotActivatedException.class).isThrownBy(
            () -> domainUserDetailsService.loadUserByUsername(USER_THREE_LOGIN));
    }

}
