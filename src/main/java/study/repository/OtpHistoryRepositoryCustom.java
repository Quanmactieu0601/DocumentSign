package study.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import study.domain.OtpHistory;

public interface OtpHistoryRepositoryCustom {
    Optional<OtpHistory> findTop1By(Long userId, String secretKey, String otp, LocalDateTime authenTime);
}
