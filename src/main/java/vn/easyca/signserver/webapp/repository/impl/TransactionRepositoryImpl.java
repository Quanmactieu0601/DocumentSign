package vn.easyca.signserver.webapp.repository.impl;

import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import vn.easyca.signserver.webapp.enm.*;
import vn.easyca.signserver.webapp.repository.TransactionRepositoryCustom;
import vn.easyca.signserver.webapp.utils.QueryUtils;
import vn.easyca.signserver.webapp.service.dto.TransactionDTO;

import javax.persistence.*;
import java.time.*;
import java.util.*;

@Repository
public class TransactionRepositoryImpl implements TransactionRepositoryCustom {
    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<TransactionDTO> findByFilter(Pageable pageable, String api, String triggerTime, TransactionStatus statusEnum, String message, String data, TransactionType typeEnum, String host, Method methodEnum, String createdBy, String fullName, LocalDateTime startDateConverted, LocalDateTime endDateConverted, Action actionEnum, Extension extensionEnum) {
        Map<String, Object> params = new HashMap<>();
        List transactionList = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("from Transaction a ");
        sqlBuilder.append("left join UserEntity b ");
        sqlBuilder.append("on a.createdBy = b.login ");
        sqlBuilder.append("WHERE 1 = 1 ");

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

        Query countQuery = entityManager.createQuery("SELECT COUNT(1) " + sqlBuilder.toString());
        QueryUtils.setParams(countQuery, params);
        Number total = (Number) countQuery.getSingleResult();
        if (total.longValue() > 0) {
            String sort = QueryUtils.addMultiSort(pageable.getSort());
            Query transactionDTOQuery = entityManager.createQuery("select a.id as id, a.api as api, a.triggerTime as triggerTime, a.status as status, a.message as message, a.data as data, a.type as type, a.method as method, a.host as host, a.action as action, a.extension as extension, " +
                "CASE WHEN b.firstName is null THEN b.lastName " +
                "     WHEN b.lastName is null THEN b.firstName " +
                "     else concat(b.lastName, ' ', b.firstName) " +
                "     end as fullName " + sqlBuilder.toString() + sort)
                .unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.aliasToBean(TransactionDTO.class));
            QueryUtils.setParamsWithPageable(transactionDTOQuery, params, pageable, total);
            transactionList = transactionDTOQuery.getResultList();
        }
        return new PageImpl<>(transactionList, pageable, total.longValue());
    }

    @Override
    public List findAllTransaction(LocalDateTime startDate, LocalDateTime endDate, TransactionType type) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sqlBuilderExport = new StringBuilder();
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

        Query transactionDTOQuery = entityManager.createQuery(" select a.id as id, a.api as api, a.triggerTime as triggerTime, a.status as status, a.message as message, a.data as data, a.type as type, a.method as method, a.host as host, a.action as action, a.extension as extension, " +
            " CASE WHEN b.firstName is null THEN b.lastName " +
            "     WHEN b.lastName is null THEN b.firstName " +
            "     else concat(b.lastName, ' ', b.firstName) " +
            "     end as fullName " + sqlBuilderExport.toString())
            .unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.aliasToBean(TransactionDTO.class));
        QueryUtils.setParams(transactionDTOQuery, params);

        return transactionDTOQuery.getResultList();
    }
}
