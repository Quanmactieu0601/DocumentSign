package vn.easyca.signserver.infrastructure.database.repositoryimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import vn.easyca.signserver.core.domain.Certificate;
import vn.easyca.signserver.core.repository.CertificateRepository;
import vn.easyca.signserver.infrastructure.database.repositoryimpl.utils.CertificateEncryptionHelper;
import vn.easyca.signserver.infrastructure.database.jpa.repository.CertificateJpaRepository;
import vn.easyca.signserver.infrastructure.database.jpa.entity.CertificateEntity;
import vn.easyca.signserver.infrastructure.database.repositoryimpl.mapper.CertificateMapper;
import vn.easyca.signserver.webapp.utils.CommonUntil;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

@Component
public class CertificateRepositoryImpl implements CertificateRepository {

    private final CertificateJpaRepository repository;

    private final CertificateMapper mapper = new CertificateMapper();

    private final CertificateEncryptionHelper encryptionHelper = new CertificateEncryptionHelper();

    @Autowired
    private EntityManager entityManager;

    public CertificateRepositoryImpl(CertificateJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Certificate getById(long id) {
        CertificateEntity entity = repository.getOne(id);
        return mapper.map(entity);
    }

    @Override
    public Certificate getBySerial(String ownerId, String serial) {
        Optional<CertificateEntity> entity = repository.getCertificateBySerial(serial);
        return entity.map(mapper::map).orElse(null);
    }

    @Override
    public Certificate save(Certificate certificate) {
        certificate = encryptionHelper.encryptCert(certificate);
        CertificateEntity entity = mapper.map(certificate);
        entity = repository.save(entity);
        return mapper.map(entity);
    }

    @Override
    public Certificate getBySerial(String serial) {
        Optional<CertificateEntity> entity = repository.getCertificateBySerial(serial);
        if (entity.isPresent()) {
            Certificate certificate = mapper.map(entity.get());
            certificate = encryptionHelper.decryptCert(certificate);
            return certificate;
        }
        return null;
    }

    @Override
    public Page<CertificateEntity> findByFilter(Pageable pageable, String alias, String ownerId, String serial, String validDate, String expiredDate) {
        Map<String, Object> params = new HashMap<>();
        List<CertificateEntity> certificateEntityList = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("FROM certificate a ");
//        String sort = Common.addSort(pageable.getSort());

        sqlBuilder.append(" WHERE 1 = 1 ");
        if (!CommonUntil.isNullOrEmptyProperty(alias)) {
            sqlBuilder.append("AND a.alias like :alias ");
            params.put("alias", "%" + alias + "%");
        }
        if (!CommonUntil.isNullOrEmptyProperty(ownerId)) {
            sqlBuilder.append("AND a.owner_id like :ownerId ");
            params.put("ownerId", "%" + ownerId + "%");
        }
        if (!CommonUntil.isNullOrEmptyProperty(serial)) {
            sqlBuilder.append("AND a.serial = :serial ");
            params.put("serial", "" + serial + "");
        }
        if (!CommonUntil.isNullOrEmptyProperty(validDate)) {
            sqlBuilder.append("AND a.valid_date >= :validDate ");
            params.put("validDate", "" + validDate + "");
        }
        if (!CommonUntil.isNullOrEmptyProperty(expiredDate)) {
            sqlBuilder.append("AND a.expired_date <= :expiredDate ");
            params.put("expiredDate", "" + expiredDate + "");
        }

        Query countQuery = entityManager.createNativeQuery("SELECT COUNT(1) " + sqlBuilder.toString());
        CommonUntil.setParams(countQuery, params);
        Number total = (Number) countQuery.getSingleResult();
        if (total.longValue() > 0) {
            Query query = entityManager.createNativeQuery("SELECT * " + sqlBuilder.toString(), CertificateEntity.class);
            CommonUntil.setParamsWithPageable(query, params, pageable, total);
            certificateEntityList = query.getResultList();
        }
        return new PageImpl<>(certificateEntityList, pageable, total.longValue());
    }

    @Override
    public boolean isExistCert(String serial) {
        try {
            Optional<CertificateEntity> entity = repository.getCertificateBySerial(serial);
            return entity.isPresent();
        } catch (Exception exception) {
            return false;
        }
    }

}
