package vn.easyca.signserver.webapp.repository.impl;

import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import vn.easyca.signserver.webapp.domain.Authority;
import vn.easyca.signserver.webapp.domain.UserEntity;
import vn.easyca.signserver.webapp.enm.*;
import vn.easyca.signserver.webapp.repository.TransactionRepositoryCustom;
import vn.easyca.signserver.webapp.repository.UserRepository;
import vn.easyca.signserver.webapp.security.SecurityUtils;
import vn.easyca.signserver.webapp.utils.AccountUtils;
import vn.easyca.signserver.webapp.utils.QueryUtils;
import vn.easyca.signserver.webapp.service.dto.TransactionDTO;

import javax.persistence.*;
import java.time.*;
import java.util.*;

@Repository
public class TransactionRepositoryImpl implements TransactionRepositoryCustom {
    @Autowired
    private EntityManager entityManager;
    private final UserRepository userRepository;

    public TransactionRepositoryImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Page<TransactionDTO> findByFilter(Pageable pageable, String api, String triggerTime, TransactionStatus statusEnum, String message, String data, TransactionType typeEnum, String host, Method methodEnum, String createdBy, String fullName, LocalDateTime startDateConverted, LocalDateTime endDateConverted, Action actionEnum, Extension extensionEnum) {
        Map<String, Object> params = new HashMap<>();
        List transactionList = new ArrayList<>();
        String acc = AccountUtils.getLoggedAccount();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("from Transaction a ");
        sqlBuilder.append("left join UserEntity b ");
        sqlBuilder.append("on a.createdBy = b.login ");
        sqlBuilder.append("WHERE 1 = 1 ");
        Optional<UserEntity> userEntityOptional = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin().get());
        if (userEntityOptional.isPresent()) {
            Set<Authority> userAuthority = userEntityOptional.get().getAuthorities();
            boolean isAdmin = userAuthority.stream().anyMatch(ua -> "ROLE_ADMIN".equals(ua.getName()) || "ROLE_SUPER_ADMIN".equals(ua.getName()));
            if (!isAdmin) {
                sqlBuilder.append("AND b.login = :login ");
                params.put("login", acc);
            }
        }
        if (actionEnum != null) {
            sqlBuilder.append("AND a.action = :action ");
            params.put("action", actionEnum);
        }

        if (startDateConverted != null) {
            sqlBuilder.append("AND a.triggerTime >= :startDate ");
            params.put("startDate", startDateConverted);
        }

        if (endDateConverted != null) {
            sqlBuilder.append("AND a.triggerTime <= :endDate ");
            params.put("endDate", endDateConverted);
        }

        if (statusEnum != null) {
            sqlBuilder.append("AND a.status = :status ");
            params.put("status", statusEnum);
        }

        if (!QueryUtils.isNullOrEmptyProperty(fullName)) {
            sqlBuilder.append("AND CONCAT(b.lastName,' ',b.firstName) like :fullName ");
            params.put("fullName", "%" + fullName + "%");
        }

        if (!QueryUtils.isNullOrEmptyProperty(api)) {
            sqlBuilder.append("AND a.api like :api ");
            params.put("api", "%" + api + "%");
        }

        if (!QueryUtils.isNullOrEmptyProperty(message)) {
            sqlBuilder.append("AND a.message like :message ");
            params.put("message", "%" + message + "%");
        }

        if (!QueryUtils.isNullOrEmptyProperty(data)) {
            sqlBuilder.append("AND a.data like :data ");
            params.put("data", "%" + data + "%");
        }

        if (typeEnum != null) {
            sqlBuilder.append("AND a.type = :type ");
            params.put("type", typeEnum);
        }

        if (!QueryUtils.isNullOrEmptyProperty(host)) {
            sqlBuilder.append("AND a.host like :host ");
            params.put("host", "%" + host + "%");
        }

        if (methodEnum != null) {
            sqlBuilder.append("AND a.method = :method ");
            params.put("method", methodEnum);
        }

        Query countQuery = entityManager.createQuery("SELECT COUNT(1) " + sqlBuilder.toString());
        QueryUtils.setParams(countQuery, params);
        Number total = (Number) countQuery.getSingleResult();
        if (total.longValue() > 0) {
            String sort = QueryUtils.addMultiSort(pageable.getSort());
            StringBuilder mainQuery = new StringBuilder();
            mainQuery.append("select new vn.easyca.signserver.webapp.service.dto.TransactionDTO");
            mainQuery.append("(a.id, a.api, a.triggerTime, a.status, a.message, a.data, a.type, a.method, a.host, a.action, a.extension, ");
            mainQuery.append("  CASE WHEN b.firstName is null THEN b.lastName ");
            mainQuery.append("  WHEN b.lastName is null THEN b.firstName ");
            mainQuery.append("  else concat(b.lastName, ' ', b.firstName) ");
            mainQuery.append("  end) ").append(sqlBuilder.toString()).append(sort);
            TypedQuery<TransactionDTO> transactionDTOQuery = entityManager.createQuery(mainQuery.toString(), TransactionDTO.class);
            QueryUtils.setParamsWithPageable(transactionDTOQuery, params, pageable, total);
            transactionList = transactionDTOQuery.getResultList();
        }
//        return new PageImpl<>(transactionList, pageable, total.longValue());
        return new PageImpl<>(transactionList, pageable, total.longValue());

    }

    @Override
    public List findAllTransaction(LocalDateTime startDate, LocalDateTime endDate, TransactionType type) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sqlBuilderExport = new StringBuilder();
        sqlBuilderExport.append("select new vn.easyca.signserver.webapp.service.dto.TransactionDTO");
        sqlBuilderExport.append("(a.id, a.api, a.triggerTime, a.status, a.message, a.data, a.type, a.method, a.host, a.action, a.extension, ");
        sqlBuilderExport.append("  CASE WHEN b.firstName is null THEN b.lastName ");
        sqlBuilderExport.append("  WHEN b.lastName is null THEN b.firstName ");
        sqlBuilderExport.append("  else concat(b.lastName, ' ', b.firstName) ");
        sqlBuilderExport.append("  end) ");
        sqlBuilderExport.append("from Transaction a ");
        sqlBuilderExport.append("left join UserEntity b ");
        sqlBuilderExport.append("on a.createdBy = b.login ");
        sqlBuilderExport.append("WHERE 1=1 ");

        if (startDate != null) {
            sqlBuilderExport.append("AND a.triggerTime >= :startDate ");
            params.put("startDate", startDate);
        }

        if (endDate != null) {
            sqlBuilderExport.append("AND a.triggerTime <= :endDate ");
            params.put("endDate", endDate);
        }
        if (type != null) {
            sqlBuilderExport.append("AND a.type = :type ");
            params.put("type", type);
        }

        TypedQuery<TransactionDTO> transactionDTOQuery = entityManager.createQuery(sqlBuilderExport.toString(), TransactionDTO.class);
        QueryUtils.setParams(transactionDTOQuery, params);

        return transactionDTOQuery.getResultList();
    }
}
