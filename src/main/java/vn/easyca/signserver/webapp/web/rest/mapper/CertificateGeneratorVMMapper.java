package vn.easyca.signserver.webapp.web.rest.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import vn.easyca.signserver.core.dto.CertificateGenerateDTO;
import vn.easyca.signserver.webapp.utils.DateTimeUtils;
import vn.easyca.signserver.webapp.web.rest.vm.request.CertificateGeneratorVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.SignElementVM;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Component
public class CertificateGeneratorVMMapper {

    public CertificateGenerateDTO map(CertificateGeneratorVM vm) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(vm, CertificateGenerateDTO.class);
    }

    public List<CertificateGenerateDTO> map(List<CertificateGeneratorVM> certificateGeneratorVMList)
    {
        List<CertificateGenerateDTO> res = new ArrayList<>();
        for (CertificateGeneratorVM vm: certificateGeneratorVMList) {
            CertificateGenerateDTO dto = map(vm);
            res.add(dto);
        }
        return res;
    }

}
