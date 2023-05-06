package infrastructure.ra;

import core.domain.CertPackage;
import core.domain.OwnerInfo;
import core.domain.RawCertificate;
import core.domain.SubjectDN;
import core.interfaces.CertificateRequester;
import java.util.List;
import org.springframework.stereotype.Component;
import ra.lib.RAServiceFade;
import ra.lib.api.RegisterCertificateApi;
import ra.lib.dto.RegisterInputDto;
import ra.lib.dto.RegisterResultDto;

@Component
public class CertificateRequesterImpl implements CertificateRequester {

    private static RAServiceFade raServiceFade;

    public static void init(RAServiceFade ra) {
        if (CertificateRequesterImpl.raServiceFade == null) raServiceFade = ra;
    }

    @Override
    public RawCertificate request(String csr, CertPackage certPackage, SubjectDN subjectDN, OwnerInfo ownerInfo)
        throws CertificateRequesterException {
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
            if (registerResultDto.getStatus() == 1) throw new Exception(registerResultDto.getMessage());
            return new RawCertificate(registerResultDto.getCertSerial(), registerResultDto.getCert());
        } catch (Exception ex) {
            throw new CertificateRequesterException(ex);
        }
    }

    @Override
    public List<RegisterResultDto> request(List<RegisterInputDto> csrs) throws CertificateRequesterException {
        try {
            RegisterCertificateApi registerCertificateApi = raServiceFade.createMultipleRegisterCertificateApi();
            List<RegisterResultDto> certs = registerCertificateApi.registerMultipleCertificates(csrs);
            return certs;
        } catch (Exception ex) {
            throw new CertificateRequesterException(ex);
        }
    }
}
