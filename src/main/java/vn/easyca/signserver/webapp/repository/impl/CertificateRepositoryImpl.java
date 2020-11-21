package vn.easyca.signserver.webapp.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.repository.CertificateRepository;
import vn.easyca.signserver.webapp.repository.CertificateRepositoryCustom;
import vn.easyca.signserver.webapp.utils.CertificateEncryptionHelper;
import vn.easyca.signserver.webapp.service.mapper.CertificateMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import vn.easyca.signserver.webapp.utils.CommonUtils;

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
        if (!CommonUtils.isNullOrEmptyProperty(alias)) {
            sqlBuilder.append("AND a.alias like :alias ");
            params.put("alias", "%" + alias + "%");
        }
        if (!CommonUtils.isNullOrEmptyProperty(ownerId)) {
            sqlBuilder.append("AND a.owner_id like :ownerId ");
            params.put("ownerId", "%" + ownerId + "%");
        }
        if (!CommonUtils.isNullOrEmptyProperty(serial)) {
            sqlBuilder.append("AND a.serial = :serial ");
            params.put("serial", "" + serial + "");
        }
        if (!CommonUtils.isNullOrEmptyProperty(validDate)) {
            sqlBuilder.append("AND a.valid_date >= :validDate ");
            params.put("validDate", "" + validDate + "");
        }
        if (!CommonUtils.isNullOrEmptyProperty(expiredDate)) {
            sqlBuilder.append("AND a.expired_date <= :expiredDate ");
            params.put("expiredDate", "" + expiredDate + "");
        }

        Query countQuery = entityManager.createNativeQuery("SELECT COUNT(1) " + sqlBuilder.toString());
        CommonUtils.setParams(countQuery, params);
        Number total = (Number) countQuery.getSingleResult();
        if (total.longValue() > 0) {
            Query query = entityManager.createNativeQuery("SELECT * " + sqlBuilder.toString(), Certificate.class);
            CommonUtils.setParamsWithPageable(query, params, pageable, total);
            certificateList = query.getResultList();
        }
        return new PageImpl<>(certificateList, pageable, total.longValue());
    }

}
