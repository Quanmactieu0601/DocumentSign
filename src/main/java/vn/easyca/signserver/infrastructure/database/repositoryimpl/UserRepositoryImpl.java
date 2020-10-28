package vn.easyca.signserver.infrastructure.database.repositoryimpl;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import vn.easyca.signserver.infrastructure.database.jpa.entity.UserEntity;
import vn.easyca.signserver.infrastructure.database.jpa.repository.UserRepositoryCustom;
import vn.easyca.signserver.webapp.utils.CommonUntil;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {
    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<UserEntity> findByFilter(Pageable pageable, String login, String account, String name, String email, String ownerId, String commonName, String country, String phone) {
        Map<String, Object> params = new HashMap<>();
        List<UserEntity> userEntityList = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("FROM jhi_user a ");
//        String sort = Common.addSort(pageable.getSort());

        sqlBuilder.append(" WHERE 1 = 1 ");
        if (!Strings.isNullOrEmpty(account)) {
            sqlBuilder.append("AND a.login like :login ");
            params.put("login", "%" + account + "%");
        }
        if (!CommonUntil.isNullOrEmptyProperty(name)) {
            sqlBuilder.append("AND a.last_name like :name ");
            params.put("name", "%" + name + "%");
        }
        if (!CommonUntil.isNullOrEmptyProperty(email)) {
            sqlBuilder.append("AND a.email like :email ");
            params.put("email", "%" + email + "%");
        }
        if (!CommonUntil.isNullOrEmptyProperty(ownerId)) {
            sqlBuilder.append("AND a.owner_id like :ownerId ");
            params.put("ownerId", "%" + ownerId + "%");
        }
        if (!CommonUntil.isNullOrEmptyProperty(commonName)) {
            sqlBuilder.append("AND a.common_name like :commonName ");
            params.put("commonName", "%" + commonName + "%");
        }
        if (!CommonUntil.isNullOrEmptyProperty(country)) {
            sqlBuilder.append("AND a.country like :country ");
            params.put("country", "%" + country + "%");
        }
        if (!CommonUntil.isNullOrEmptyProperty(phone)) {
            sqlBuilder.append("AND a.phone like :phone ");
            params.put("phone", "%" + phone + "%");
        }
        Query countQuery = entityManager.createNativeQuery("SELECT COUNT(1) " + sqlBuilder.toString());
        CommonUntil.setParams(countQuery, params);
        Number total = (Number) countQuery.getSingleResult();
        if (total.longValue() > 0) {
            Query query = entityManager.createNativeQuery("SELECT * " + sqlBuilder.toString(), UserEntity.class);
            CommonUntil.setParamsWithPageable(query, params, pageable, total);
            userEntityList = query.getResultList();
        }
        return new PageImpl<>(userEntityList, pageable, total.longValue());

    }

    ;

}
