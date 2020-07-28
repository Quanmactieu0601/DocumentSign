package vn.easyca.signserver.webapp.web.rest.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import vn.easyca.signserver.webapp.service.dto.CertificateGeneratorDto;
import vn.easyca.signserver.webapp.web.rest.vm.CertificateGeneratorVM;

@Component
public class CertificateGeneratorVMMapper {

    public CertificateGeneratorDto map(CertificateGeneratorVM vm) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(vm, CertificateGeneratorDto.class);
    }
}
