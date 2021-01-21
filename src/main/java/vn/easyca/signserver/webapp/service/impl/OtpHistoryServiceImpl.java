package vn.easyca.signserver.webapp.service.impl;

import org.springframework.data.repository.query.Param;
import vn.easyca.signserver.webapp.service.OtpHistoryService;
import vn.easyca.signserver.webapp.domain.OtpHistory;
import vn.easyca.signserver.webapp.repository.OtpHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing {@link OtpHistory}.
 */
@Service
@Transactional
public class OtpHistoryServiceImpl implements OtpHistoryService {

    private final Logger log = LoggerFactory.getLogger(OtpHistoryServiceImpl.class);

    private final OtpHistoryRepository otpHistoryRepository;

    public OtpHistoryServiceImpl(OtpHistoryRepository otpHistoryRepository) {
        this.otpHistoryRepository = otpHistoryRepository;
    }

    /**
     * Save a otpHistory.
     *
     * @param otpHistory the entity to save.
     * @return the persisted entity.
     */
    @Override
    public OtpHistory save(OtpHistory otpHistory) {
        log.debug("Request to save OtpHistory : {}", otpHistory);
        return otpHistoryRepository.save(otpHistory);
    }

    /**
     * Get all the otpHistories.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<OtpHistory> findAll() {
        log.debug("Request to get all OtpHistories");
        return otpHistoryRepository.findAll();
    }


    /**
     * Get one otpHistory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<OtpHistory> findOne(Long id) {
        log.debug("Request to get OtpHistory : {}", id);
        return otpHistoryRepository.findById(id);
    }

    /**
     * Delete the otpHistory by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete OtpHistory : {}", id);
        otpHistoryRepository.deleteById(id);
    }

    @Override
    public Optional<OtpHistory> findTop1By(Long userId, String secretKey, String otp, LocalDateTime authenTime) {
        return otpHistoryRepository.findTop1By(userId, secretKey, otp, authenTime);
    }
}
