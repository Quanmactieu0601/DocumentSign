package vn.easyca.signserver.webapp.service.impl;

import org.springframework.cache.annotation.CacheEvict;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.enm.SystemConfigKey;
import vn.easyca.signserver.webapp.service.SystemConfigCachingService;
import vn.easyca.signserver.webapp.service.SystemConfigService;
import vn.easyca.signserver.webapp.domain.SystemConfig;
import vn.easyca.signserver.webapp.repository.SystemConfigRepository;
import vn.easyca.signserver.webapp.service.dto.SystemConfigDTO;
import vn.easyca.signserver.webapp.service.mapper.SystemConfigMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link SystemConfig}.
 */
@Service
@Transactional
public class SystemConfigServiceImpl implements SystemConfigService {

    private final Logger log = LoggerFactory.getLogger(SystemConfigServiceImpl.class);

    private final SystemConfigRepository systemConfigRepository;

    private final SystemConfigMapper systemConfigMapper;


    public SystemConfigServiceImpl(SystemConfigRepository systemConfigRepository, SystemConfigMapper systemConfigMapper) {
        this.systemConfigRepository = systemConfigRepository;
        this.systemConfigMapper = systemConfigMapper;
    }

    /**
     * Save a systemConfig.
     *
     * @param systemConfigDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public SystemConfigDTO save(SystemConfigDTO systemConfigDTO) throws ApplicationException {
        log.debug("Request to save SystemConfig : {}", systemConfigDTO);
        SystemConfig systemConfig = systemConfigMapper.toEntity(systemConfigDTO);
        Optional<SystemConfigDTO> temp = this.findByComIdAndKey(systemConfigDTO.getComId(), systemConfigDTO.getKey());
        if (temp.isPresent() && systemConfigDTO.getId() == null) {
            throw new ApplicationException("Duplicate ComId and Key!");
        } else systemConfig = systemConfigRepository.save(systemConfig);
        return systemConfigMapper.toDto(systemConfig);
    }

    /**
     * Get all the systemConfigs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SystemConfigDTO> findAll(Pageable pageable) {
        log.debug("Request to get all SystemConfigs");
        return systemConfigRepository.findAll(pageable)
            .map(systemConfigMapper::toDto);
    }


    /**
     * Get one systemConfig by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<SystemConfigDTO> findOne(Long id) {
        log.debug("Request to get SystemConfig : {}", id);
        return systemConfigRepository.findById(id)
            .map(systemConfigMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SystemConfigDTO> findByComIdAndKey(Long comId, SystemConfigKey key) {
        log.debug("Request to get SystemConfig : {} ", comId, " and ", key);
        return systemConfigRepository.findByComIdAndKey(comId, key)
            .map(systemConfigMapper::toDto);
    }

    /**
     * Delete the systemConfig by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete SystemConfig : {}", id);
        systemConfigRepository.deleteById(id);
    }

    @Override
    public List<SystemConfigDTO> findAllByActivatedIsTrue() {
        return systemConfigRepository.findAllByActivatedIsTrue().stream()
            .map(systemConfigMapper::toDto)
            .collect(Collectors.toList());
    }


}
