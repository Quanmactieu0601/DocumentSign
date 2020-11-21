package vn.easyca.signserver.webapp.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import vn.easyca.signserver.webapp.web.rest.TestUtil;

public class SignatureTemplateTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SignatureTemplate.class);
        SignatureTemplate signatureTemplate1 = new SignatureTemplate();
        signatureTemplate1.setId(1L);
        SignatureTemplate signatureTemplate2 = new SignatureTemplate();
        signatureTemplate2.setId(signatureTemplate1.getId());
        assertThat(signatureTemplate1).isEqualTo(signatureTemplate2);
        signatureTemplate2.setId(2L);
        assertThat(signatureTemplate1).isNotEqualTo(signatureTemplate2);
        signatureTemplate1.setId(null);
        assertThat(signatureTemplate1).isNotEqualTo(signatureTemplate2);
    }
}
