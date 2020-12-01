package vn.easyca.signserver.webapp.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import vn.easyca.signserver.webapp.web.rest.TestUtil;

public class SignatureImageTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SignatureImage.class);
        SignatureImage signatureImage1 = new SignatureImage();
        signatureImage1.setId(1L);
        SignatureImage signatureImage2 = new SignatureImage();
        signatureImage2.setId(signatureImage1.getId());
        assertThat(signatureImage1).isEqualTo(signatureImage2);
        signatureImage2.setId(2L);
        assertThat(signatureImage1).isNotEqualTo(signatureImage2);
        signatureImage1.setId(null);
        assertThat(signatureImage1).isNotEqualTo(signatureImage2);
    }
}
