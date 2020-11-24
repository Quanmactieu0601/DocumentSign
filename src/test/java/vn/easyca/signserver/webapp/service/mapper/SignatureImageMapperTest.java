package vn.easyca.signserver.webapp.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class SignatureImageMapperTest {

    private SignatureImageMapper signatureImageMapper;

    @BeforeEach
    public void setUp() {
        signatureImageMapper = new SignatureImageMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(signatureImageMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(signatureImageMapper.fromId(null)).isNull();
    }
}
