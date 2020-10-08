package vn.easyca.signserver.webapp.web.rest.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.application.dto.CertificateGenerateDTO;
import vn.easyca.signserver.webapp.web.rest.vm.request.CertificateGeneratorVM;

@Service
public class CertificateGeneratorResultVMMapper {

    public CertificateGenerateDTO map(CertificateGeneratorVM vm) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(vm, CertificateGenerateDTO.class);
    }
}
