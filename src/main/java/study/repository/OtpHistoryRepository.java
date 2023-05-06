package study.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import study.domain.OtpHistory;

@Repository
public interface OtpHistoryRepository extends R2dbcRepository<OtpHistory, Long>, OtpHistoryRepositoryCustom {}
