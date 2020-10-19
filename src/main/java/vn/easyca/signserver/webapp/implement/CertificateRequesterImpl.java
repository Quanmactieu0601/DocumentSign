package vn.easyca.signserver.webapp.implement;

import org.springframework.stereotype.Component;
import vn.easyca.signserver.application.dependency.CertificateRequester;
import vn.easyca.signserver.ra.RAServiceFade;
import vn.easyca.signserver.ra.api.RegisterCertificateApi;
import vn.easyca.signserver.ra.dto.RegisterInputDto;
import vn.easyca.signserver.ra.dto.RegisterResultDto;
import vn.easyca.signserver.application.domain.CertPackage;
import vn.easyca.signserver.application.domain.OwnerInfo;
import vn.easyca.signserver.application.domain.SubjectDN;
import vn.easyca.signserver.application.domain.RawCertificate;
import vn.easyca.signserver.application.services.CertificateGenerateService;
import vn.easyca.signserver.webapp.config.Constants;

@Component
public class CertificateRequesterImpl implements CertificateRequester {

    private static RAServiceFade raServiceFade;

    @Override
    public RawCertificate request(String csr, CertPackage certPackage, SubjectDN subjectDN, OwnerInfo ownerInfo) throws CertificateRequesterException {
        try {
            RegisterCertificateApi registerCertificateApi = getRAService().createRegisterCertificateApi();
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
            RegisterResultDto registerResultDto = registerCertificateApi.register(registerInputDto);
            if (registerResultDto.getStatus() == 1)
                throw new Exception(registerResultDto.getMessage());
            return new RawCertificate(registerResultDto.getCertSerial(), registerResultDto.getCert());
        } catch (Exception ex) {
            throw new CertificateRequesterException(ex);
        }
    }

    private synchronized RAServiceFade getRAService() {
        if (raServiceFade == null) {
            raServiceFade = RAServiceFade.getInstance();
            raServiceFade.init(Constants.RA_CONFIG);
        }
        return raServiceFade;
    }

}
