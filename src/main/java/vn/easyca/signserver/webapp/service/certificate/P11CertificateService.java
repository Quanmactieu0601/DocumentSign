package vn.easyca.signserver.webapp.service.certificate;

import vn.easyca.signserver.core.cryptotoken.Config;
import vn.easyca.signserver.core.cryptotoken.CryptoToken;
import vn.easyca.signserver.core.cryptotoken.P11CryptoToken;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.domain.CertificateType;
import vn.easyca.signserver.webapp.domain.TokenInfo;
import vn.easyca.signserver.webapp.repository.CertificateRepository;
import vn.easyca.signserver.webapp.service.Encryption;
import vn.easyca.signserver.webapp.service.config.DomainConfigRegistration;
import vn.easyca.signserver.webapp.service.dto.CertificateGeneratorDto;
import vn.easyca.signserver.webapp.service.dto.NewCertificateInfo;
import vn.easyca.signserver.webapp.service.error.CreateCertificateException;
import vn.easyca.signserver.webapp.service.error.GenCertificateInputException;
import vn.easyca.signserver.webapp.service.utils.DataValidator;

import java.util.Base64;

public class P11CertificateService extends CertificateService {

    P11CertificateService(CertificateRepository certificateRepository, Encryption encryption) {
        super(certificateRepository,encryption);
    }

    public NewCertificateInfo genCertificate(CertificateGeneratorDto dto) throws CreateCertificateException, GenCertificateInputException {
        validGenCertificateDto(dto);
        CryptoToken cryptoToken = new P11CryptoToken();
        String alias = "MST:" + dto.getOwnerId();
        Config cfg = DomainConfigRegistration.getInstance().getPKC11ConfigForGenKeyPair();
        try {
            cryptoToken.init(DomainConfigRegistration.getInstance().getPKC11ConfigForGenKeyPair());
            cryptoToken.genKeyPair(alias, dto.getKeyLen());
            Certificate certificate = new Certificate();
            certificate.setAlias(alias);
            TokenInfo tokenInfo = new TokenInfo();
            tokenInfo.setLibrary(cfg.getLibrary());
            tokenInfo.setSlot(Integer.parseInt(cfg.getSlot()));
            certificate.setCertificateTokenInfo(tokenInfo);
            certificate.setTokenType(CertificateType.PKCS11.toString());
            certificateRepository.save(certificate);
            String base64 = Base64.getEncoder().encodeToString(certificate.getX509Certificate().getEncoded());
            String serial = certificate.getSerial();
            return new NewCertificateInfo(base64,serial);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CreateCertificateException(e.getMessage());
        }
    }

    private void validGenCertificateDto(CertificateGeneratorDto dto) throws GenCertificateInputException {
        if (DataValidator.isNullOrEmpty(dto.getC()))
            throw new GenCertificateInputException("C filed is not empty");
        if (DataValidator.isNullOrEmpty(dto.getCn()))
            throw new GenCertificateInputException("CN filed is not empty");
        if (DataValidator.isNullOrEmpty(dto.getC()))
            throw new GenCertificateInputException("CN filed is not empty");
        if (DataValidator.isNullOrEmpty(dto.getOwnerId()))
            throw new GenCertificateInputException("CN filed is not empty");
    }
}
