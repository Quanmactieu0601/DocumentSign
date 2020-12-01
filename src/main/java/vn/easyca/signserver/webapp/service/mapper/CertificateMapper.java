package vn.easyca.signserver.webapp.service.mapper;
import vn.easyca.signserver.core.domain.CertificateDTO;
import vn.easyca.signserver.core.domain.TokenInfo;
import vn.easyca.signserver.webapp.domain.Certificate;

// TODO: chuyển mapper về chuẩn
public class CertificateMapper {

    public CertificateDTO map(Certificate entity) {
        if (entity == null)
            return null;
        CertificateDTO certificateDTO = new CertificateDTO();
        certificateDTO.setAlias(entity.getAlias());
        certificateDTO.setId(entity.getId());
        certificateDTO.setOwnerId(entity.getOwnerId());
        certificateDTO.setRawData(entity.getRawData());
        certificateDTO.setSerial(entity.getRawData());
        certificateDTO.setSubjectInfo(entity.getSubjectInfo());
        certificateDTO.setTokenType(entity.getTokenType());
        certificateDTO.setTokenInfo(TokenInfo.createInstance(entity.getTokenInfo()));
        certificateDTO.setValidDate(entity.getValidDate());
        certificateDTO.setExpiredDate(entity.getExpiredDate());
        certificateDTO.setActiveStatus(entity.getActiveStatus());
        certificateDTO.setSignatureImageId(entity.getSignatureImageId());
        certificateDTO.setEncryptedPin(entity.getEncryptedPin());
        return certificateDTO;
    }

    // TODO: mapping TH này sẽ sai nếu là get object ra để update
    public Certificate map(CertificateDTO certificateDTO) {
        if (certificateDTO == null)
            return null;
        Certificate entity = new Certificate();
        entity.setAlias(certificateDTO.getAlias());
        entity.setId(certificateDTO.getId());
        entity.setOwnerId(certificateDTO.getOwnerId());
        entity.setRawData(certificateDTO.getRawData());
        entity.setSerial(certificateDTO.getSerial());
        entity.setSubjectInfo(certificateDTO.getSubjectInfo());
        entity.setTokenType(certificateDTO.getTokenType());
        entity.setTokenInfo(certificateDTO.getTokenInfo().toString());
        entity.setValidDate(certificateDTO.getValidDate());
        entity.setExpiredDate(certificateDTO.getExpiredDate());
        entity.setActiveStatus(certificateDTO.getActiveStatus());
        certificateDTO.setSignatureImageId(entity.getSignatureImageId());
        entity.setEncryptedPin(certificateDTO.getEncryptedPin());
        return entity;
    }


}
