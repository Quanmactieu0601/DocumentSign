package vn.easyca.signserver.infrastructure.ra;

import org.springframework.stereotype.Component;
import vn.easyca.signserver.core.interfaces.CertificateRequester;
import vn.easyca.signserver.ra.lib.RAServiceFade;
import vn.easyca.signserver.ra.lib.api.RegisterCertificateApi;
import vn.easyca.signserver.ra.lib.dto.RegisterInputDto;
import vn.easyca.signserver.ra.lib.dto.RegisterResultDto;
import vn.easyca.signserver.core.domain.CertPackage;
import vn.easyca.signserver.core.domain.OwnerInfo;
import vn.easyca.signserver.core.domain.SubjectDN;
import vn.easyca.signserver.core.domain.RawCertificate;
import vn.easyca.signserver.webapp.config.Constants;

@Component
public class CertificateRequesterImpl implements CertificateRequester {

    private static RAServiceFade raServiceFade;

    public static void init(RAServiceFade ra) {
        if (CertificateRequesterImpl.raServiceFade == null)
            raServiceFade=ra;

    }

    @Override
    public RawCertificate request(String csr, CertPackage certPackage, SubjectDN subjectDN, OwnerInfo ownerInfo) throws CertificateRequesterException {
        try {
            RegisterCertificateApi registerCertificateApi = raServiceFade.createRegisterCertificateApi();
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
}
