package vn.easyca.signserver.webapp.service.mapper;


import vn.easyca.signserver.webapp.domain.*;
import vn.easyca.signserver.webapp.service.dto.SignatureImageDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link SignatureImage} and its DTO {@link SignatureImageDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface SignatureImageMapper extends EntityMapper<SignatureImageDTO, SignatureImage> {



    default SignatureImage fromId(Long id) {
        if (id == null) {
            return null;
        }
        SignatureImage signatureImage = new SignatureImage();
        signatureImage.setId(id);
        return signatureImage;
    }
}
