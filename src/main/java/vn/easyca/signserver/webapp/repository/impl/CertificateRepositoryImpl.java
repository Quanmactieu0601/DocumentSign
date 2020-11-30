package vn.easyca.signserver.webapp.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import vn.easyca.signserver.infrastructure.database.jpa.entity.CertificateEntity_;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.repository.CertificateRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import vn.easyca.signserver.webapp.utils.QueryUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

@Repository
public class CertificateRepositoryImpl implements CertificateRepositoryCustom {
    @Autowired
    private EntityManager entityManager;

//    @Override
//    public vn.easyca.signserver.core.domain.Certificate getBySerial(String serial) {
//        Optional<Certificate> entity = repository.getCertificateBySerial(serial);
//        if (entity.isPresent()) {
//            vn.easyca.signserver.core.domain.Certificate certificate = mapper.map(entity.get());
//            certificate = encryptionHelper.decryptCert(certificate);
//            return certificate;
//        }
//        return null;
//    }

    @Override
    public Page<Certificate> findByFilter(Pageable pageable, String alias, String ownerId, String serial, String validDate, String expiredDate) {
        Map<String, Object> params = new HashMap<>();
        List<Certificate> certificateList = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("FROM certificate a ");
//        String sort = Common.addSort(pageable.getSort());

        sqlBuilder.append(" WHERE 1 = 1 ");
        if (!QueryUtils.isNullOrEmptyProperty(alias)) {
            sqlBuilder.append("AND a.alias like :alias ");
            params.put("alias", "%" + alias + "%");
        }
        if (!QueryUtils.isNullOrEmptyProperty(ownerId)) {
            sqlBuilder.append("AND a.owner_id like :ownerId ");
            params.put("ownerId", "%" + ownerId + "%");
        }
        if (!QueryUtils.isNullOrEmptyProperty(serial)) {
            sqlBuilder.append("AND a.serial = :serial ");
            params.put("serial", "" + serial + "");
        }
        if (!QueryUtils.isNullOrEmptyProperty(validDate)) {
            sqlBuilder.append("AND a.valid_date >= :validDate ");
            params.put("validDate", "" + validDate + "");
        }
        if (!QueryUtils.isNullOrEmptyProperty(expiredDate)) {
            sqlBuilder.append("AND a.expired_date <= :expiredDate ");
            params.put("expiredDate", "" + expiredDate + "");
        }

        Query countQuery = entityManager.createNativeQuery("SELECT COUNT(1) " + sqlBuilder.toString());
        QueryUtils.setParams(countQuery, params);
        Number total = (Number) countQuery.getSingleResult();
        if (total.longValue() > 0) {
            String sort = QueryUtils.addMultiSort(pageable.getSort());
            Query query = entityManager.createNativeQuery("SELECT * " + sqlBuilder.toString() + sort, Certificate.class);
            QueryUtils.setParamsWithPageable(query, params, pageable, total);
            certificateList = query.getResultList();
        }
        return new PageImpl<>(certificateList, pageable, total.longValue());
    }

}
