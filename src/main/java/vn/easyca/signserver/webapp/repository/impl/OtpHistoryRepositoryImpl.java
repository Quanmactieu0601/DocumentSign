package vn.easyca.signserver.webapp.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import vn.easyca.signserver.webapp.domain.OtpHistory;
import vn.easyca.signserver.webapp.enm.*;
import vn.easyca.signserver.webapp.repository.OtpHistoryRepositoryCustom;
import vn.easyca.signserver.webapp.service.dto.TransactionDTO;
import vn.easyca.signserver.webapp.utils.QueryUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class OtpHistoryRepositoryImpl implements OtpHistoryRepositoryCustom {
    @Autowired
    private EntityManager entityManager;

    @Override
    public Optional<OtpHistory> findTop1By(Long userId, String secretKey, String otp, LocalDateTime authenTime) {
        StringBuilder strQuery = new StringBuilder();
        strQuery.append("select a from OtpHistory a ");
        strQuery.append("where a.userId = :userId and a.secretKey = :secretKey and a.otp = :otp ");
        strQuery.append("and a.actionTime <= :authenTime and a.expireTime >= :authenTime ORDER BY a.actionTime desc");
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("secretKey", secretKey);
        params.put("otp", otp);
        params.put("authenTime", authenTime);

        Query query = entityManager.createQuery(strQuery.toString());
        QueryUtils.setParams(query, params);

        Object otpHistory = query.getResultStream()
            .findFirst()
            .orElse(null);
        return otpHistory == null ? Optional.empty() :  Optional.of((OtpHistory) otpHistory);
    }
}
