package vn.easyca.signserver.webapp.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import vn.easyca.signserver.webapp.jpa.entity.CertificateEntity;
import vn.easyca.signserver.webapp.web.rest.TestUtil;

public class CertificateEntityTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CertificateEntity.class);
        CertificateEntity certificate1 = new CertificateEntity();
        certificate1.setId(1L);
        CertificateEntity certificate2 = new CertificateEntity();
        certificate2.setId(certificate1.getId());
        assertThat(certificate1).isEqualTo(certificate2);
        certificate2.setId(2L);
        assertThat(certificate1).isNotEqualTo(certificate2);
        certificate1.setId(null);
        assertThat(certificate1).isNotEqualTo(certificate2);
    }
}
