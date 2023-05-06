//package study.repository.impl;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Repository;
//import study.repository.SignatureTemplateRepositoryCustom;
//import study.service.dto.SignatureTemplateDTO;
//import study.utils.QueryUtils;
//
//import javax.persistence.EntityManager;
//import javax.persistence.Query;
//import javax.persistence.TypedQuery;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Repository
//public class SignatureTemplateRepositoryImpl implements SignatureTemplateRepositoryCustom {
//
//    @Autowired
//    private EntityManager entityManager;
//
//    @Override
//    public Page<SignatureTemplateDTO> findAllSignatureTemplate(Pageable pageable) {
//        List signatureTemplateList = new ArrayList<>();
//        Map<String, Object> params = new HashMap<>();
//        StringBuilder sqlBuilder = new StringBuilder();
//
//        sqlBuilder.append("from SignatureTemplate a ");
//        sqlBuilder.append("left join UserEntity b ");
//        sqlBuilder.append("on a.userId = b.id ");
//        sqlBuilder.append("WHERE 1 = 1 ");
//
//        Query countQuery = entityManager.createQuery("SELECT COUNT(1) " + sqlBuilder.toString());
//        QueryUtils.setParams(countQuery, params);
//        Number total = (Number) countQuery.getSingleResult();
//
//        if (total.longValue() > 0) {
//            String sort = QueryUtils.addMultiSort(pageable.getSort());
//            StringBuilder mainQuery = new StringBuilder();
//            mainQuery.append("select new vn.easyca.signserver.webapp.service.dto.SignatureTemplateDTO");
//            mainQuery.append("(a.id, a.userId, a.createdBy, a.createdDate, a.htmlTemplate, a.coreParser, a.width, a.height, a.thumbnail, a.activated, ");
//            mainQuery.append("  CASE WHEN b.firstName is null THEN b.lastName ");
//            mainQuery.append("  WHEN b.lastName is null THEN b.firstName ");
//            mainQuery.append("  else concat(b.lastName, ' ', b.firstName) ");
//            mainQuery.append("  end) ");
//            mainQuery.append("from SignatureTemplate a ");
//            mainQuery.append("left join UserEntity b ");
//            mainQuery.append("on a.userId = b.id ");
//            mainQuery.append("WHERE 1=1 ");
//            TypedQuery<SignatureTemplateDTO> signatureTemplateDTOTypedQuery = entityManager.createQuery(mainQuery.toString(), SignatureTemplateDTO.class);
//            QueryUtils.setParamsWithPageable(signatureTemplateDTOTypedQuery, params, pageable, total);
//            signatureTemplateList = signatureTemplateDTOTypedQuery.getResultList();
//        }
//        return new PageImpl<>(signatureTemplateList, pageable, total.longValue());
//    }
//
//    @Override
//    public Page<SignatureTemplateDTO> findAllSignatureTemplateByUserId(Pageable pageable, Long userId) {
//        List signatureTemplateList = new ArrayList<>();
//        Map<String, Object> params = new HashMap<>();
//        StringBuilder sqlBuilder = new StringBuilder();
//
//        sqlBuilder.append("from SignatureTemplate a ");
//        sqlBuilder.append("left join UserEntity b ");
//        sqlBuilder.append("on a.userId = b.id ");
//        sqlBuilder.append("WHERE a.userId = :userId");
//        params.put("userId", userId);
//
//        Query countQuery = entityManager.createQuery("SELECT COUNT(1) " + sqlBuilder.toString());
//        QueryUtils.setParams(countQuery, params);
//        Number total = (Number) countQuery.getSingleResult();
//
//        if (total.longValue() > 0) {
//            StringBuilder mainQuery = new StringBuilder();
//            mainQuery.append("select new vn.easyca.signserver.webapp.service.dto.SignatureTemplateDTO");
//            mainQuery.append("(a.id, a.userId, a.createdBy, a.createdDate, a.htmlTemplate, a.coreParser, a.width, a.height, a.thumbnail, a.activated, ");
//            mainQuery.append("  CASE WHEN b.firstName is null THEN b.lastName ");
//            mainQuery.append("  WHEN b.lastName is null THEN b.firstName ");
//            mainQuery.append("  else concat(b.lastName, ' ', b.firstName) ");
//            mainQuery.append("  end) ");
//            mainQuery.append("from SignatureTemplate a ");
//            mainQuery.append("left join UserEntity b ");
//            mainQuery.append("on a.userId = b.id ");
//            mainQuery.append("WHERE a.userId = :userId");
//            params.put("userId", userId);
//
//            TypedQuery<SignatureTemplateDTO> signatureTemplateDTOTypedQuery = entityManager.createQuery(mainQuery.toString(), SignatureTemplateDTO.class);
//            QueryUtils.setParamsWithPageable(signatureTemplateDTOTypedQuery, params, pageable, total);
//            signatureTemplateList = signatureTemplateDTOTypedQuery.getResultList();
//        }
//        return new PageImpl<>(signatureTemplateList, pageable, total.longValue());
//    }
//}
//
