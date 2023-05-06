package study.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import study.domain.OtpHistory;

/**
 * Service Interface for managing {@link OtpHistory}.
 */
public interface OtpHistoryService {
    /**
     * Save a otpHistory.
     *
     * @param otpHistory the entity to save.
     * @return the persisted entity.
     */
    OtpHistory save(OtpHistory otpHistory);

    /**
     * Get all the otpHistories.
     *
     * @return the list of entities.
     */
    List<OtpHistory> findAll();

    /**
     * Get the "id" otpHistory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<OtpHistory> findOne(Long id);

    /**
     * Delete the "id" otpHistory.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    Optional<OtpHistory> findTop1By(Long userId, String secretKey, String otp, LocalDateTime authenTime);
}
