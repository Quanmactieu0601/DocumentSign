package vn.easyca.signserver.webapp.service.mapper;


import vn.easyca.signserver.webapp.domain.*;
import vn.easyca.signserver.webapp.service.dto.CertPackageDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link CertPackage} and its DTO {@link CertPackageDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface CertPackageMapper extends EntityMapper<CertPackageDTO, CertPackage> {



    default CertPackage fromId(Long id) {
        if (id == null) {
            return null;
        }
        CertPackage certPackage = new CertPackage();
        certPackage.setId(id);
        return certPackage;
    }
}
