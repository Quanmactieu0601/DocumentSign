package vn.easyca.signserver.webapp.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import vn.easyca.signserver.webapp.web.rest.TestUtil;

public class SystemConfigCategoryTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SystemConfigCategory.class);
        SystemConfigCategory systemConfigCategory1 = new SystemConfigCategory();
        systemConfigCategory1.setId(1L);
        SystemConfigCategory systemConfigCategory2 = new SystemConfigCategory();
        systemConfigCategory2.setId(systemConfigCategory1.getId());
        assertThat(systemConfigCategory1).isEqualTo(systemConfigCategory2);
        systemConfigCategory2.setId(2L);
        assertThat(systemConfigCategory1).isNotEqualTo(systemConfigCategory2);
        systemConfigCategory1.setId(null);
        assertThat(systemConfigCategory1).isNotEqualTo(systemConfigCategory2);
    }
}
