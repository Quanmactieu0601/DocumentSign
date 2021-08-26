package vn.easyca.signserver.webapp.service.mapper;
import org.springframework.stereotype.Component;
import vn.easyca.signserver.core.domain.CertificateDTO;
import vn.easyca.signserver.core.domain.TokenInfo;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.utils.CertificateEncryptionHelper;

// TODO: chuyển mapper về chuẩn
@Component
public class CertificateMapper {

    private final CertificateEncryptionHelper encryptionHelper;

    public CertificateMapper(CertificateEncryptionHelper encryptionHelper) {
        this.encryptionHelper = encryptionHelper;
    }

    public CertificateDTO map(Certificate entity) {
        if (entity == null)
            return null;
        CertificateDTO certificateDTO = new CertificateDTO();
        certificateDTO.setAlias(entity.getAlias());
        certificateDTO.setId(entity.getId());
        certificateDTO.setOwnerId(entity.getOwnerId());
        certificateDTO.setRawData(entity.getRawData());
        certificateDTO.setSerial(entity.getSerial());
        certificateDTO.setSubjectInfo(entity.getSubjectInfo());
        certificateDTO.setTokenType(entity.getTokenType());
        certificateDTO.setTokenInfo(TokenInfo.createInstance(entity.getTokenInfo()));
        certificateDTO.setValidDate(entity.getValidDate());
        certificateDTO.setExpiredDate(entity.getExpiredDate());
        certificateDTO.setActiveStatus(entity.getActiveStatus());
        certificateDTO.setSignatureImageId(entity.getSignatureImageId());
        certificateDTO.setEncryptedPin(entity.getEncryptedPin());
        certificateDTO.setSecretKey(entity.getSecretKey());
        certificateDTO.setPackageId(entity.getPackageId());
        certificateDTO.setSignedTurnCount(entity.getSignedTurnCount());
        //TODO: update decrypt
        certificateDTO = encryptionHelper.decryptCert(certificateDTO);
        return certificateDTO;
    }

    public Certificate map(CertificateDTO certificateDTO) {
        if (certificateDTO == null)
            return null;

        //TODO: update decrypt
        certificateDTO = encryptionHelper.encryptCert(certificateDTO);

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
        entity.setSignatureImageId(certificateDTO.getSignatureImageId());
        entity.setEncryptedPin(certificateDTO.getEncryptedPin());
        entity.setSecretKey(certificateDTO.getSecretKey());
        return entity;
    }

}
