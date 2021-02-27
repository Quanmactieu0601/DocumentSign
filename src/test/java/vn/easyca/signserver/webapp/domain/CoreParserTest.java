package vn.easyca.signserver.webapp.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import vn.easyca.signserver.webapp.web.rest.TestUtil;

public class CoreParserTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CoreParser.class);
        CoreParser coreParser1 = new CoreParser();
        coreParser1.setId(1L);
        CoreParser coreParser2 = new CoreParser();
        coreParser2.setId(coreParser1.getId());
        assertThat(coreParser1).isEqualTo(coreParser2);
        coreParser2.setId(2L);
        assertThat(coreParser1).isNotEqualTo(coreParser2);
        coreParser1.setId(null);
        assertThat(coreParser1).isNotEqualTo(coreParser2);
    }
}
