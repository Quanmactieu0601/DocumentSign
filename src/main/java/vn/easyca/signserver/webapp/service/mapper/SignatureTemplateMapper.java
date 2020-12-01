package vn.easyca.signserver.webapp.service.mapper;


import vn.easyca.signserver.webapp.domain.*;
import vn.easyca.signserver.webapp.service.dto.SignatureTemplateDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link SignatureTemplate} and its DTO {@link SignatureTemplateDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface SignatureTemplateMapper extends EntityMapper<SignatureTemplateDTO, SignatureTemplate> {



    default SignatureTemplate fromId(Long id) {
        if (id == null) {
            return null;
        }
        SignatureTemplate signatureTemplate = new SignatureTemplate();
        signatureTemplate.setId(id);
        return signatureTemplate;
    }
}
