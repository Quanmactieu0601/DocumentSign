package vn.easyca.signserver.webapp.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class SystemConfigMapperTest {

    private SystemConfigMapper systemConfigMapper;

    @BeforeEach
    public void setUp() {
        systemConfigMapper = new SystemConfigMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(systemConfigMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(systemConfigMapper.fromId(null)).isNull();
    }
}
