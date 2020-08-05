package vn.easyca.signserver.webapp.adapter;

import com.google.gson.Gson;
import org.springframework.stereotype.Component;
import vn.easyca.signserver.ca.api.api.CAFacadeApi;
import vn.easyca.signserver.ca.api.api.RegisterCertificateApi;
import vn.easyca.signserver.ca.api.api.dto.RegisterInputDto;
import vn.easyca.signserver.ca.api.api.dto.RegisterResultDto;
import vn.easyca.signserver.business.domain.CertPackage;
import vn.easyca.signserver.business.domain.OwnerInfo;
import vn.easyca.signserver.business.domain.SubjectDN;
import vn.easyca.signserver.business.domain.RawCertificate;
import vn.easyca.signserver.business.services.CertGenService;

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
        registerInputDto.genHash();
        System.out.println(new Gson().toJson(registerInputDto));
        RegisterResultDto registerResultDto = registerCertificateApi.register(registerInputDto);
        if (registerResultDto.getStatus() == 1) {
            throw new Exception(registerResultDto.getMessage());
        }
        return new RawCertificate(registerResultDto.getCertSerial(), registerResultDto.getCert());
    }
}
