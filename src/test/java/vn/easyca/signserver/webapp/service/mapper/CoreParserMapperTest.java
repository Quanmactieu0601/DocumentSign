package vn.easyca.signserver.webapp.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class CoreParserMapperTest {

    private CoreParserMapper coreParserMapper;

    @BeforeEach
    public void setUp() {
        coreParserMapper = new CoreParserMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(coreParserMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(coreParserMapper.fromId(null)).isNull();
    }
}
