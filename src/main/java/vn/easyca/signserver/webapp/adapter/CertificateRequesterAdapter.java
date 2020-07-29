package vn.easyca.signserver.webapp.adapter;

import org.springframework.stereotype.Component;
import vn.easyca.signserver.ca.api.api.CAFacadeApi;
import vn.easyca.signserver.ca.api.api.RegisterCertificateApi;
import vn.easyca.signserver.ca.api.api.dto.RegisterInputDto;
import vn.easyca.signserver.ca.api.api.dto.RegisterResultDto;
import vn.easyca.signserver.core.domain.CertPackage;
import vn.easyca.signserver.core.domain.OwnerInfo;
import vn.easyca.signserver.core.domain.SubjectDN;
import vn.easyca.signserver.core.domain.RawCertificate;
import vn.easyca.signserver.core.services.CertGenService;

@Component
public class CertificateRequesterAdapter implements CertGenService.CertificateRequester {



    @Override
    public RawCertificate request(String csr, CertPackage certPackage, SubjectDN subjectDN, OwnerInfo ownerInfo) throws Exception {
        RegisterCertificateApi registerCertificateApi = CAFacadeApi.getInstance().createRegisterCertificateApi();
        RegisterInputDto registerInputDto = new RegisterInputDto();
        registerInputDto.setCsr(csr);
        registerInputDto.setCertMethod(certPackage.getCertMethod());
        registerInputDto.setCertProfile(certPackage.getCertProfile());
        registerInputDto.setCertProfileType(certPackage.getCertProfileType());
        registerInputDto.setCn(subjectDN.getCn());
        registerInputDto.setCustomerEmail(ownerInfo.getOwnerEmail());
        registerInputDto.setCustomerPhone(ownerInfo.getOwnerPhone());
        registerInputDto.setId(ownerInfo.getOwnerId());
        registerInputDto.setO(subjectDN.getO());
        registerInputDto.setOu(subjectDN.getOu());
        registerInputDto.setSt(subjectDN.getS());
        RegisterResultDto registerResultDto = registerCertificateApi.register(registerInputDto);
        if (registerResultDto.getStatus() == 1) {
            throw new Exception(registerResultDto.getMessage());
        }
        return new RawCertificate(registerResultDto.getCert(), registerResultDto.getCertSerial());
    }
}
