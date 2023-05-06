//package study.repository.impl;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Repository;
//import study.domain.User;
//import study.repository.UserRepositoryCustom;
//import study.utils.QueryUtils;
//
//import javax.persistence.EntityManager;
//import javax.persistence.Query;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Repository
//public class UserRepositoryImpl implements UserRepositoryCustom {
//    @Autowired
//    private EntityManager entityManager;
//
//    @Override
//    public Page<User> findByFilter(Pageable pageable, String login, String account, String name, String email, String ownerId, String commonName, String country, String phone, boolean activated) {
//        Map<String, Object> params = new HashMap<>();
//        List<User> userEntityList = new ArrayList<>();
//        StringBuilder sqlBuilder = new StringBuilder();
//        sqlBuilder.append("FROM UserEntity a ");
////        String sort = Common.addSort(pageable.getSort());
//
//        sqlBuilder.append(" WHERE 1 = 1 ");
//        if (!QueryUtils.isNullOrEmptyProperty(account)) {
//            sqlBuilder.append("AND a.login like :login ");
//            params.put("login", "%" + account.trim() + "%");
//        }
//        if (!QueryUtils.isNullOrEmptyProperty(name)) {
//            sqlBuilder.append("AND concat(a.firstName, ' ', a.lastName) like :name ");
//            params.put("name", "%" + name.trim() + "%");
//        }
//        if (!QueryUtils.isNullOrEmptyProperty(email)) {
//            sqlBuilder.append("AND a.email like :email ");
//            params.put("email",  email.trim() );
//        }
//        if (!QueryUtils.isNullOrEmptyProperty(ownerId)) {
//            sqlBuilder.append("AND a.ownerId like :ownerId ");
//            params.put("ownerId", "%" + ownerId.trim() + "%");
//        }
//        if (!QueryUtils.isNullOrEmptyProperty(commonName)) {
//            sqlBuilder.append("AND a.commonName like :commonName ");
//            params.put("commonName", "%" + commonName.trim() + "%");
//        }
//        if (!QueryUtils.isNullOrEmptyProperty(country)) {
//            sqlBuilder.append("AND a.country like :country ");
//            params.put("country", "%" + country.trim() + "%");
//        }
//        if (!QueryUtils.isNullOrEmptyProperty(phone)) {
//            sqlBuilder.append("AND a.phone like :phone ");
//            params.put("phone", "%" + phone.trim() + "%");
//        }
//        if (activated) {
//            sqlBuilder.append("And a.activated = true ");
//        }
//        Query countQuery = entityManager.createQuery("SELECT COUNT(1) " + sqlBuilder.toString());
//        QueryUtils.setParams(countQuery, params);
//        Number total = (Number) countQuery.getSingleResult();
//        if (total.longValue() > 0) {
//            String sort = QueryUtils.addMultiSort(pageable.getSort());
//            Query query = entityManager.createQuery("SELECT a " + sqlBuilder.toString() + sort, User.class);
//            QueryUtils.setParamsWithPageable(query, params, pageable, total);
//            userEntityList = query.getResultList();
//        }
//        return new PageImpl<>(userEntityList, pageable, total.longValue());
//    }
//}
//
