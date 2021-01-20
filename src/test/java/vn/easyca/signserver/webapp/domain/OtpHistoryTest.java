package vn.easyca.signserver.webapp.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import vn.easyca.signserver.webapp.web.rest.TestUtil;

public class OtpHistoryTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OtpHistory.class);
        OtpHistory otpHistory1 = new OtpHistory();
        otpHistory1.setId(1L);
        OtpHistory otpHistory2 = new OtpHistory();
        otpHistory2.setId(otpHistory1.getId());
        assertThat(otpHistory1).isEqualTo(otpHistory2);
        otpHistory2.setId(2L);
        assertThat(otpHistory1).isNotEqualTo(otpHistory2);
        otpHistory1.setId(null);
        assertThat(otpHistory1).isNotEqualTo(otpHistory2);
    }
}
