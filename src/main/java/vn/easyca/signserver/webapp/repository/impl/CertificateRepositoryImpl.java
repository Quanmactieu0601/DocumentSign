package vn.easyca.signserver.webapp.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.repository.CertificateRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import vn.easyca.signserver.webapp.utils.QueryUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
public class CertificateRepositoryImpl implements CertificateRepositoryCustom {
    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<Certificate> findByFilter(Pageable pageable, String alias, String ownerId, String serial, String validDate, String expiredDate) {
        Map<String, Object> params = new HashMap<>();
        List<Certificate> certificateList = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("FROM Certificate a ");
//        String sort = Common.addSort(pageable.getSort());

        sqlBuilder.append(" WHERE 1 = 1 ");
        if (!QueryUtils.isNullOrEmptyProperty(alias)) {
            sqlBuilder.append("AND a.alias like :alias ");
            params.put("alias", "%" + alias + "%");
        }
        if (!QueryUtils.isNullOrEmptyProperty(ownerId)) {
            sqlBuilder.append("AND a.ownerId like :ownerId ");
            params.put("ownerId", "%" + ownerId + "%");
        }
        if (!QueryUtils.isNullOrEmptyProperty(serial)) {
            sqlBuilder.append("AND a.serial = :serial ");
            params.put("serial", serial);
        }
        if (!QueryUtils.isNullOrEmptyProperty(validDate)) {
            String validDatebonus = validDate + " 00:00";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(validDatebonus, formatter);
            sqlBuilder.append("AND a.validDate >= :validDate ");
            params.put("validDate", dateTime);
        }
        if (!QueryUtils.isNullOrEmptyProperty(expiredDate)) {
            String validDatebonus = validDate + " 23:59";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(validDatebonus, formatter);
            sqlBuilder.append("AND a.expiredDate <= :expiredDate ");
            params.put("expiredDate", dateTime);
        }

        Query countQuery = entityManager.createQuery("SELECT COUNT(1) " + sqlBuilder.toString());
        QueryUtils.setParams(countQuery, params);
        Number total = (Number) countQuery.getSingleResult();
        if (total.longValue() > 0) {
            String sort = QueryUtils.addMultiSort(pageable.getSort());
            TypedQuery query = entityManager.createQuery("SELECT a " + sqlBuilder.toString() + sort, Certificate.class);
            QueryUtils.setParamsWithPageable(query, params, pageable, total);
            certificateList = query.getResultList();
        }
        return new PageImpl<>(certificateList, pageable, total.longValue());
    }

}
