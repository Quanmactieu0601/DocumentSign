package vn.easyca.signserver.webapp.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import vn.easyca.signserver.webapp.web.rest.TestUtil;

public class SignatureTemplateDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SignatureTemplateDTO.class);
        SignatureTemplateDTO signatureTemplateDTO1 = new SignatureTemplateDTO();
        signatureTemplateDTO1.setId(1L);
        SignatureTemplateDTO signatureTemplateDTO2 = new SignatureTemplateDTO();
        assertThat(signatureTemplateDTO1).isNotEqualTo(signatureTemplateDTO2);
        signatureTemplateDTO2.setId(signatureTemplateDTO1.getId());
        assertThat(signatureTemplateDTO1).isEqualTo(signatureTemplateDTO2);
        signatureTemplateDTO2.setId(2L);
        assertThat(signatureTemplateDTO1).isNotEqualTo(signatureTemplateDTO2);
        signatureTemplateDTO1.setId(null);
        assertThat(signatureTemplateDTO1).isNotEqualTo(signatureTemplateDTO2);
    }
}
