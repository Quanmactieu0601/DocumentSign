package vn.easyca.signserver.webapp.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class SignatureTemplateMapperTest {

    private SignatureTemplateMapper signatureTemplateMapper;

    @BeforeEach
    public void setUp() {
        signatureTemplateMapper = new SignatureTemplateMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(signatureTemplateMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(signatureTemplateMapper.fromId(null)).isNull();
    }
}
