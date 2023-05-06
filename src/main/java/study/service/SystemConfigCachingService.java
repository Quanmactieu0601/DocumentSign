package study.service;

import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import study.config.SystemDbConfiguration;
import study.service.dto.SystemConfigDTO;
import vn.easyca.signserver.core.exception.ApplicationException;

@Service
public class SystemConfigCachingService {

    public static final String SYSTEM_CONFIG_CACHE = "system-config";

    private final SystemConfigService systemConfigService;

    public SystemConfigCachingService(SystemConfigService systemConfigService) {
        this.systemConfigService = systemConfigService;
    }

    private SystemDbConfiguration init() throws ApplicationException {
        List<SystemConfigDTO> systemConfigDTOList = systemConfigService.findAllByActivatedIsTrue();
        return SystemDbConfiguration.init(systemConfigDTOList);
    }

    @Cacheable(SYSTEM_CONFIG_CACHE)
    public SystemDbConfiguration getConfig() throws ApplicationException {
        return init();
    }

    @CacheEvict(SYSTEM_CONFIG_CACHE)
    public void clearCache() {}

    /**
     * update system-config cache
     * call this method when update System configuration record
     * @return
     * @throws ApplicationException
     */
    @CachePut(SYSTEM_CONFIG_CACHE)
    public SystemDbConfiguration reloadAndGetConfig() throws ApplicationException {
        return init();
    }
}
