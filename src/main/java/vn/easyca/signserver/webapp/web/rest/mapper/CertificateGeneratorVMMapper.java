package vn.easyca.signserver.webapp.web.rest.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import vn.easyca.signserver.core.dto.CertificateGenerateDTO;
import vn.easyca.signserver.webapp.web.rest.vm.request.CertificateGeneratorVM;

@Component
public class CertificateGeneratorVMMapper {

    public CertificateGenerateDTO map(CertificateGeneratorVM vm) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(vm, CertificateGenerateDTO.class);
    }
}
