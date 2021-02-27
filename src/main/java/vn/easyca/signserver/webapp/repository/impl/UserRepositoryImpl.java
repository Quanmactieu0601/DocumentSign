package vn.easyca.signserver.webapp.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import vn.easyca.signserver.webapp.domain.UserEntity;
import vn.easyca.signserver.webapp.enm.TransactionType;
import vn.easyca.signserver.webapp.repository.UserRepositoryCustom;
import vn.easyca.signserver.webapp.service.dto.TransactionDTO;
import vn.easyca.signserver.webapp.service.dto.UserDTO;
import vn.easyca.signserver.webapp.service.dto.UserDropdownDTO;
import vn.easyca.signserver.webapp.utils.QueryUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {
    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<UserEntity> findByFilter(Pageable pageable, String login, String account, String name, String email, String ownerId, String commonName, String country, String phone) {
        Map<String, Object> params = new HashMap<>();
        List<UserEntity> userEntityList = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("FROM UserEntity a ");
//        String sort = Common.addSort(pageable.getSort());

        sqlBuilder.append(" WHERE 1 = 1 ");
        if (!QueryUtils.isNullOrEmptyProperty(account)) {
            sqlBuilder.append("AND a.login like :login ");
            params.put("login", "%" + account + "%");
        }
        if (!QueryUtils.isNullOrEmptyProperty(name)) {
            sqlBuilder.append("AND concat(a.firstName, a.lastName) like :name ");
            params.put("name", "%" + name + "%");
        }
        if (!QueryUtils.isNullOrEmptyProperty(email)) {
            sqlBuilder.append("AND a.email like :email ");
            params.put("email",  email );
        }
        if (!QueryUtils.isNullOrEmptyProperty(ownerId)) {
            sqlBuilder.append("AND a.ownerId like :ownerId ");
            params.put("ownerId", "%" + ownerId + "%");
        }
        if (!QueryUtils.isNullOrEmptyProperty(commonName)) {
            sqlBuilder.append("AND a.commonName like :commonName ");
            params.put("commonName", "%" + commonName + "%");
        }
        if (!QueryUtils.isNullOrEmptyProperty(country)) {
            sqlBuilder.append("AND a.country like :country ");
            params.put("country", "%" + country + "%");
        }
        if (!QueryUtils.isNullOrEmptyProperty(phone)) {
            sqlBuilder.append("AND a.phone like :phone ");
            params.put("phone", "%" + phone + "%");
        }
        Query countQuery = entityManager.createQuery("SELECT COUNT(1) " + sqlBuilder.toString());
        QueryUtils.setParams(countQuery, params);
        Number total = (Number) countQuery.getSingleResult();
        if (total.longValue() > 0) {
            String sort = QueryUtils.addMultiSort(pageable.getSort());
            Query query = entityManager.createQuery("SELECT a " + sqlBuilder.toString() + sort, UserEntity.class);
            QueryUtils.setParamsWithPageable(query, params, pageable, total);
            userEntityList = query.getResultList();
        }
        return new PageImpl<>(userEntityList, pageable, total.longValue());
    }

    @Override
    public List getAllUserForDropdown() {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sqlBuilderExport = new StringBuilder();
        sqlBuilderExport.append("select new vn.easyca.signserver.webapp.service.dto.UserDropdownDTO");
        sqlBuilderExport.append(" (a.id, a.login) ");
        sqlBuilderExport.append("from UserEntity a");
        sqlBuilderExport.append(" WHERE 1=1 ");
        TypedQuery<UserDropdownDTO> typedQuery = entityManager.createQuery(sqlBuilderExport.toString(), UserDropdownDTO.class);

        return typedQuery.getResultList();
    }
}
