package vn.easyca.signserver.webapp.service.certificate;

import vn.easyca.signserver.core.cryptotoken.Config;
import vn.easyca.signserver.core.cryptotoken.P12CryptoToken;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.domain.CertificateType;
import vn.easyca.signserver.webapp.domain.TokenInfo;
import vn.easyca.signserver.webapp.repository.CertificateRepository;
import vn.easyca.signserver.webapp.service.dto.ImportCertificateDto;
import vn.easyca.signserver.webapp.service.error.CreateCertificateException;
import vn.easyca.signserver.webapp.service.Encryption;

import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;

public class P12CertificateService extends CertificateService {

    private final Encryption encryption;

    P12CertificateService(CertificateRepository certificateRepository, Encryption encryption) {
        super(certificateRepository);
        this.encryption = encryption;
    }

    @Override
    public Certificate importCertificate(ImportCertificateDto dto) throws CreateCertificateException {

        P12CryptoToken p12CryptoToken = new P12CryptoToken();
        Config config = new Config();
        byte[] fileContent = Base64.getDecoder().decode(dto.getP12Base64());
        config.initPkcs12(new ByteArrayInputStream(fileContent), dto.getPin());
        try {
            p12CryptoToken.init(config);
            List<String> aliases = p12CryptoToken.getAliases();
            String alias = aliases.get(0);
            X509Certificate x509Certificate = (X509Certificate) p12CryptoToken.getCertificate(aliases.get(0));
            String serial = x509Certificate.getSerialNumber().toString(16);
            Certificate certificate = new Certificate();
            certificate.rawData(Base64.getEncoder().encodeToString(x509Certificate.getEncoded()));
            certificate.setOwnerId(dto.getOwnerId());
            certificate.setSerial(serial);
            certificate.setAlias(alias);
            certificate.tokenType(CertificateType.PKCS12.toString());
            certificate.setCertificateTokenInfo(getTokenInfo(dto.getP12Base64()));
            certificateRepository.save(certificate);
            return certificate;
        } catch (Exception exception) {
            log.error(exception.getLocalizedMessage());
            throw new CreateCertificateException(exception.getMessage());
        }
    }

    private TokenInfo getTokenInfo(String data) throws Encryption.EncryptionException {

        data = encryption.encrypt(data);
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setData(data);
        return tokenInfo;
    }
}
