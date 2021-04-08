package vn.easyca.signserver.webapp.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import vn.easyca.signserver.webapp.web.rest.TestUtil;

public class CoreParserDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CoreParserDTO.class);
        CoreParserDTO coreParserDTO1 = new CoreParserDTO();
        coreParserDTO1.setId(1L);
        CoreParserDTO coreParserDTO2 = new CoreParserDTO();
        assertThat(coreParserDTO1).isNotEqualTo(coreParserDTO2);
        coreParserDTO2.setId(coreParserDTO1.getId());
        assertThat(coreParserDTO1).isEqualTo(coreParserDTO2);
        coreParserDTO2.setId(2L);
        assertThat(coreParserDTO1).isNotEqualTo(coreParserDTO2);
        coreParserDTO1.setId(null);
        assertThat(coreParserDTO1).isNotEqualTo(coreParserDTO2);
    }
}
