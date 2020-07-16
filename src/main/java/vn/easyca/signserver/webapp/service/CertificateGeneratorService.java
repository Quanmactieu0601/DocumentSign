package vn.easyca.signserver.webapp.service;


import io.github.jhipster.security.RandomUtil;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.service.certificate.CertificateService;
import vn.easyca.signserver.webapp.service.dto.*;
import vn.easyca.signserver.webapp.service.error.CreateCertificateException;
import vn.easyca.signserver.webapp.service.error.GenCertificateInputException;

// complex service
public class CertificateGeneratorService {

    private CertificateService certificateService;

    private UserService userService;

    public CertificateGeneratedResult genCertificate(CertificateGeneratorDto dto) throws Exception {
        try {
            NewCertificateInfo certificateInfo = null;
            certificateInfo = certificateService.genCertificate(dto);
            CertificateGeneratedResult result = new CertificateGeneratedResult(true);
            result.setNewCertificateInfo(certificateInfo)
                .setNewAccount(createNewAccount(dto));
            return result;
        } catch (CertificateService.NotImplementedException | CreateCertificateException | GenCertificateInputException e) {
          // write log
            throw e;
        }
    }

    private NewAccount createNewAccount(CertificateGeneratorDto dto) {

        String password = RandomUtil.generatePassword();
        UserDTO userDTO = new UserDTO();
        userDTO.setLogin(dto.getOwnerId());
        userDTO.setLangKey("vi");
        userDTO.setActivated(true);
        userDTO.setCreatedBy("system");
        userService.createUser(userDTO, password);
        return new NewAccount(dto.getOwnerId(), password);
    }


}
