package vn.easyca.signserver.webapp.web.rest.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import vn.easyca.signserver.core.dto.CertificateGenerateDTO;
import vn.easyca.signserver.webapp.utils.DateTimeUtils;
import vn.easyca.signserver.webapp.web.rest.vm.request.CertificateGeneratorVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.register.RegisterCertVM;
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

    public CertificateGenerateDTO map(RegisterCertVM certVM) {
        ModelMapper modelMapper = new ModelMapper();
        CertificateGenerateDTO cert = modelMapper.map(certVM, CertificateGenerateDTO.class);
        cert.setL(certVM.getLocality());
        cert.setS(certVM.getState());
        cert.setO(certVM.getOrganization());
        cert.setT(certVM.getTitle());
        cert.setCn(certVM.getCommonName());
        return cert;
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

    public List<CertificateGenerateDTO> mapFromRegisterCertVM(List<RegisterCertVM> registerCertVMList) {
        List<CertificateGenerateDTO> res = new ArrayList<>();
        for (RegisterCertVM vm: registerCertVMList) {
            CertificateGenerateDTO dto = map(vm);
            res.add(dto);
        }
        return res;
    }
}
