package vn.easyca.signserver.webapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.domain.OtpHistory;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpHistoryRepositoryCustom {
    Optional<OtpHistory> findTop1By(Long userId, String secretKey, String otp,
                                    LocalDateTime authenTime);
}
