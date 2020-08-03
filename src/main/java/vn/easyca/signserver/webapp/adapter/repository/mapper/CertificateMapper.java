package vn.easyca.signserver.webapp.adapter.repository.mapper;


import vn.easyca.signserver.core.domain.Certificate;
import vn.easyca.signserver.core.domain.TokenInfo;
import vn.easyca.signserver.webapp.jpa.entity.CertificateEntity;

public class CertificateMapper {

    public Certificate map(CertificateEntity entity) {
        if (entity == null)
            return null;
        Certificate certificate = new Certificate();
        certificate.setAlias(entity.getAlias());
        certificate.setId(entity.getId());
        certificate.setOwnerId(entity.getOwnerId());
        certificate.setRawData(entity.getRawData());
        certificate.setSerial(entity.getRawData());
        certificate.setSubjectInfo(entity.getSubjectInfo());
        certificate.setTokenType(entity.getTokenType());
        certificate.setTokenInfo(TokenInfo.createInstance(entity.getTokenInfo()));
        return certificate;
    }

    public CertificateEntity map(Certificate certificate) {
        if (certificate == null)
            return null;
        CertificateEntity entity = new CertificateEntity();
        entity.setAlias(certificate.getAlias());
        entity.setId(certificate.getId());
        entity.setOwnerId(certificate.getOwnerId());
        entity.setRawData(certificate.getRawData());
        entity.setSerial(certificate.getSerial());
        entity.setSubjectInfo(certificate.getSubjectInfo());
        entity.setTokenType(certificate.getTokenType());
        entity.setTokenInfo(certificate.getTokenInfo().toString());
        return entity;
    }


}
