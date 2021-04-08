package vn.easyca.signserver.webapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.easyca.signserver.webapp.service.dto.SignatureTemplateDTO;


public interface SignatureTemplateRepositoryCustom {
    Page<SignatureTemplateDTO> findAllSignatureTemplate(Pageable pageable);
}
