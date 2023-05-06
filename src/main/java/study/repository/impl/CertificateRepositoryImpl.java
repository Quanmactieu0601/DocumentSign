//package study.repository.impl;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Repository;
//import study.domain.Certificate;
//import study.repository.CertificateRepositoryCustom;
//import study.utils.QueryUtils;
//
//import javax.persistence.EntityManager;
//import javax.persistence.Query;
//import javax.persistence.TypedQuery;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Repository
//public class CertificateRepositoryImpl implements CertificateRepositoryCustom {
//    @Autowired
//    private EntityManager entityManager;
//
//    @Override
//    public Page<Certificate> findByFilter(Pageable pageable, String alias, String ownerId, String serial, String validDate, String expiredDate, Integer type) {
//        Map<String, Object> params = new HashMap<>();
//        List<Certificate> certificateList = new ArrayList<>();
//        StringBuilder sqlBuilder = new StringBuilder();
//        sqlBuilder.append("FROM Certificate a ");
////        String sort = Common.addSort(pageable.getSort());
//
//        sqlBuilder.append(" WHERE 1 = 1 ");
//        if (!QueryUtils.isNullOrEmptyProperty(alias)) {
//            sqlBuilder.append("AND a.alias like :alias ");
//            params.put("alias", "%" + alias + "%");
//        }
//        if (!QueryUtils.isNullOrEmptyProperty(ownerId)) {
//            sqlBuilder.append("AND a.ownerId = :ownerId ");
//            params.put("ownerId",ownerId);
//        }
//        if (!QueryUtils.isNullOrEmptyProperty(serial)) {
//            sqlBuilder.append("AND a.serial = :serial ");
//            params.put("serial", serial);
//        }
//        if (!QueryUtils.isNullOrEmptyProperty(validDate)) {
//            sqlBuilder.append("AND a.validDate >= :validDate ");
////            params.put("validDate", dateTime);
//            LocalDateTime localDateTime = LocalDate.parse(validDate, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
//            params.put("validDate", localDateTime);
//        }
//        if (!QueryUtils.isNullOrEmptyProperty(expiredDate)) {
//            sqlBuilder.append("AND a.expiredDate <= :expiredDate ");
////            params.put("expiredDate", dateTime);
//            LocalDateTime localDateTime = LocalDate.parse(expiredDate, DateTimeFormatter.ISO_LOCAL_DATE).atTime(23, 59, 59);
//            params.put("expiredDate", localDateTime);
//        }
//        if (type != null) {
//            sqlBuilder.append("AND a.type =: type");
//            params.put("type", type);
//        }else{
//            sqlBuilder.append("AND a.type = 0");
//        }
//        Query countQuery = entityManager.createQuery("SELECT COUNT(1) " + sqlBuilder.toString());
//        QueryUtils.setParams(countQuery, params);
//        Number total = (Number) countQuery.getSingleResult();
//        if (total.longValue() > 0) {
//            String sort = QueryUtils.addMultiSort(pageable.getSort());
//            TypedQuery query = entityManager.createQuery("SELECT a " + sqlBuilder.toString() + sort, Certificate.class);
//            QueryUtils.setParamsWithPageable(query, params, pageable, total);
//            certificateList = query.getResultList();
//        }
//        return new PageImpl<>(certificateList, pageable, total.longValue());
//    }
//
//}
//
