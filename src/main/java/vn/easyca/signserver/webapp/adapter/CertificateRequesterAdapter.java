package vn.easyca.signserver.webapp.adapter;

import org.springframework.stereotype.Component;
import vn.easyca.signserver.ca.service.api.CAFacadeApi;
import vn.easyca.signserver.ca.service.api.RegisterCertificateApi;
import vn.easyca.signserver.ca.service.api.dto.RegisterInputDto;
import vn.easyca.signserver.ca.service.api.dto.RegisterResultDto;
import vn.easyca.signserver.webapp.service.cert_generator.CertPackage;
import vn.easyca.signserver.webapp.service.cert_generator.OwnerInfo;
import vn.easyca.signserver.webapp.service.cert_generator.SubjectDN;
import vn.easyca.signserver.webapp.service.domain.RawCertificate;
import vn.easyca.signserver.webapp.service.port.CertificateRequester;

@Component
public class CertificateRequesterAdapter implements CertificateRequester {



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
