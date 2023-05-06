package study.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import study.enums.SystemConfigKey;
import study.service.dto.SystemConfigDTO;
import vn.easyca.signserver.core.exception.ApplicationException;

/**
 * Service Interface for managing {@link study.domain.SystemConfig}.
 */
public interface SystemConfigService {
    /**
     * Save a systemConfig.
     *
     * @param systemConfigDTO the entity to save.
     * @return the persisted entity.
     */
    SystemConfigDTO save(SystemConfigDTO systemConfigDTO) throws ApplicationException;

    /**
     * Get all the systemConfigs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SystemConfigDTO> findAll(Pageable pageable);

    List<SystemConfigDTO> findAllByActivatedIsTrue();

    /**
     * Get the "id" systemConfig.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SystemConfigDTO> findOne(Long id);

    Optional<SystemConfigDTO> findByComIdAndKey(Long comId, SystemConfigKey key);

    /**
     * Delete the "id" systemConfig.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
