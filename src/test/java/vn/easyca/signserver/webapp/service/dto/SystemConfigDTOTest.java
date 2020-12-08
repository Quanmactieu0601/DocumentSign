package vn.easyca.signserver.webapp.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import vn.easyca.signserver.webapp.web.rest.TestUtil;

public class SystemConfigDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SystemConfigDTO.class);
        SystemConfigDTO systemConfigDTO1 = new SystemConfigDTO();
        systemConfigDTO1.setId(1L);
        SystemConfigDTO systemConfigDTO2 = new SystemConfigDTO();
        assertThat(systemConfigDTO1).isNotEqualTo(systemConfigDTO2);
        systemConfigDTO2.setId(systemConfigDTO1.getId());
        assertThat(systemConfigDTO1).isEqualTo(systemConfigDTO2);
        systemConfigDTO2.setId(2L);
        assertThat(systemConfigDTO1).isNotEqualTo(systemConfigDTO2);
        systemConfigDTO1.setId(null);
        assertThat(systemConfigDTO1).isNotEqualTo(systemConfigDTO2);
    }
}
