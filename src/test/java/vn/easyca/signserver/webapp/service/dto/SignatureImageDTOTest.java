package vn.easyca.signserver.webapp.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import vn.easyca.signserver.webapp.web.rest.TestUtil;

public class SignatureImageDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SignatureImageDTO.class);
        SignatureImageDTO signatureImageDTO1 = new SignatureImageDTO();
        signatureImageDTO1.setId(1L);
        SignatureImageDTO signatureImageDTO2 = new SignatureImageDTO();
        assertThat(signatureImageDTO1).isNotEqualTo(signatureImageDTO2);
        signatureImageDTO2.setId(signatureImageDTO1.getId());
        assertThat(signatureImageDTO1).isEqualTo(signatureImageDTO2);
        signatureImageDTO2.setId(2L);
        assertThat(signatureImageDTO1).isNotEqualTo(signatureImageDTO2);
        signatureImageDTO1.setId(null);
        assertThat(signatureImageDTO1).isNotEqualTo(signatureImageDTO2);
    }
}
