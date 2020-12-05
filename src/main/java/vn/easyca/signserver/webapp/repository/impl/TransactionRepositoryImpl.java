package vn.easyca.signserver.webapp.repository.impl;

import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import vn.easyca.signserver.webapp.repository.TransactionRepositoryCustom;
import vn.easyca.signserver.webapp.utils.DateTimeUtils;
import vn.easyca.signserver.webapp.utils.QueryUtils;
import vn.easyca.signserver.webapp.service.dto.TransactionDTO;

import javax.persistence.*;
import java.text.ParseException;
import java.time.*;
import java.util.*;

@Repository
public class TransactionRepositoryImpl implements TransactionRepositoryCustom {
    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<TransactionDTO> findByFilter(Pageable pageable, String api, String triggerTime, String status, String message, String data, String type, String host, String method, String createdBy, String fullName, String startDate, String endDate, String action, String extension) throws ParseException {
        Map<String, Object> params = new HashMap<>();
        List transactionList = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("from Transaction a ");
        sqlBuilder.append("left join UserEntity b ");
        sqlBuilder.append("on a.createdBy = b.login ");
        sqlBuilder.append("WHERE 1 = 1 ");

        if (!QueryUtils.isNullOrEmptyProperty(api)) {
            sqlBuilder.append("AND a.api like :api ");
            params.put("api", "%" + api + "%");
        }
        if (!QueryUtils.isNullOrEmptyProperty(startDate)) {
            LocalDateTime startDateConverted = DateTimeUtils.convertToLocalDateTime(startDate);
            sqlBuilder.append("AND a.triggerTime >= :startDate ");
            params.put("startDate", startDateConverted);
        }
        if (!QueryUtils.isNullOrEmptyProperty(endDate)) {
            LocalDateTime endDateConverted = DateTimeUtils.convertToLocalDateTime(endDate);
            sqlBuilder.append("AND a.triggerTime <= :endDate ");
            params.put("endDate", endDateConverted);
        }
        if (!QueryUtils.isNullOrEmptyProperty(status)) {
            Boolean a = null;
            if (status.equals("Thành Công")){
                a = true;
            }else if ((status.equals("Thất Bại"))){
                a = false;
            }
            sqlBuilder.append("AND a.status =: status ");
            params.put("status", a);
        }
        if (!QueryUtils.isNullOrEmptyProperty(message)) {
            sqlBuilder.append("AND a.message like :message ");
            params.put("message", "%" + message + "%");
        }
        if (!QueryUtils.isNullOrEmptyProperty(data)) {
            sqlBuilder.append("AND a.data like :data ");
            params.put("data", "%" + data + "%");
        }
        if (!QueryUtils.isNullOrEmptyProperty(type)) {
            sqlBuilder.append("AND a.type like :type ");
            params.put("type", "%" + type + "%");
        }
        if (!QueryUtils.isNullOrEmptyProperty(host)) {
            sqlBuilder.append("AND a.host like :host ");
            params.put("host", "%" + host + "%");
        }
        if (!QueryUtils.isNullOrEmptyProperty(method)) {
            sqlBuilder.append("AND a.method like :method ");
            params.put("method", "%" + method + "%");
        }
        if (!QueryUtils.isNullOrEmptyProperty(fullName)) {
            sqlBuilder.append("AND CONCAT(b.lastName,' ',b.firstName) like :fullName ");
            params.put("fullName", "%" + fullName + "%");
        }
        if (!QueryUtils.isNullOrEmptyProperty(action)) {
            sqlBuilder.append("AND a.action like :action ");
            params.put("action", "%" + action + "%");
        }
        if (!QueryUtils.isNullOrEmptyProperty(extension)) {
            sqlBuilder.append("AND a.extension like :extension ");
            params.put("extension", "%" + extension + "%");
        }

        Query countQuery = entityManager.createQuery("SELECT COUNT(1) " + sqlBuilder.toString());
        QueryUtils.setParams(countQuery, params);
        Number total = (Number) countQuery.getSingleResult();
        if (total.longValue() > 0) {
            String sort = QueryUtils.addMultiSort(pageable.getSort());
            Query transactionDTOList = entityManager.createQuery("select a.id as id, a.api as api, a.triggerTime as triggerTime, a.status as status, a.message as message, a.data as data, a.type as type, a.method as method, a.host as host, a.action as action, a.extension as extension, CONCAT(b.lastName,' ',b.firstName) as fullName " + sqlBuilder.toString() + sort)
                .unwrap(org.hibernate.query.Query.class).setResultTransformer(Transformers.aliasToBean(TransactionDTO.class));
            QueryUtils.setParamsWithPageable(transactionDTOList, params, pageable, total);
            transactionList = transactionDTOList.getResultList();
        }
        return new PageImpl<>(transactionList, pageable, total.longValue());
    }
}
