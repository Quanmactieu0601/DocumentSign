package study.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import study.service.dto.SignatureTemplateDTO;

public interface SignatureTemplateRepositoryCustom {
    Page<SignatureTemplateDTO> findAllSignatureTemplate(Pageable pageable);
    Page<SignatureTemplateDTO> findAllSignatureTemplateByUserId(Pageable pageable, Long userId);
}
