package vn.easyca.signserver.application.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.application.dto.ImportP12FileDTO;
import vn.easyca.signserver.application.dependency.UserCreator;
import vn.easyca.signserver.application.exception.ApplicationException;
import vn.easyca.signserver.application.exception.CertificateAppException;
import vn.easyca.signserver.pki.cryptotoken.Config;
import vn.easyca.signserver.pki.cryptotoken.P12CryptoToken;
import vn.easyca.signserver.application.domain.Certificate;
import vn.easyca.signserver.application.domain.TokenInfo;
import vn.easyca.signserver.application.repository.CertificateRepository;
import vn.easyca.signserver.application.utils.CommonUtils;
import vn.easyca.signserver.pki.cryptotoken.error.*;

import java.io.ByteArrayInputStream;
import java.security.KeyStoreException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;

@Service
public class P12ImportService {

    private final CertificateRepository repository;

    private final UserCreator userCreator;

    private final Log log = LogFactory.getLog(P12ImportService.class);

    @Autowired
    public P12ImportService(CertificateRepository repository, UserCreator userCreator) {
        this.repository = repository;
        this.userCreator = userCreator;
    }

    public Certificate insert(ImportP12FileDTO input) throws ApplicationException {
        P12CryptoToken p12CryptoToken = new P12CryptoToken();
        Config config = new Config();
        byte[] fileContent = Base64.getDecoder().decode(input.getP12Base64());
        config.initPkcs12(new ByteArrayInputStream(fileContent), input.getPin());
        try {
            p12CryptoToken.init(config);
        } catch (InitCryptoTokenException e) {
            throw new CertificateAppException(e);
        }
        String alias = null;
        try {
            alias = getAlias(input.getAliasName(), p12CryptoToken);
        } catch (CryptoTokenException e) {
            throw new CertificateAppException("Can not get alias from certificate", e);
        }

        X509Certificate x509Certificate = null;
        try {
            x509Certificate = (X509Certificate) p12CryptoToken.getCertificate(alias);
        } catch (KeyStoreException e) {
            throw new CertificateAppException("certificate has error", e);
        }
        String serial = x509Certificate.getSerialNumber().toString(16);
        String base64Cert = null;
        try {
            base64Cert = CommonUtils.encodeBase64X509(x509Certificate);
        } catch (CertificateEncodingException e) {
            throw new CertificateAppException("certificate encoding exception", e);
        }
        Certificate certificate = new Certificate();
        certificate.setRawData(base64Cert);
        certificate.setOwnerId(input.getOwnerId());
        certificate.setSerial(serial);
        certificate.setAlias(alias);
        certificate.setTokenType(Certificate.PKCS_12);
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setData(input.getP12Base64());
        certificate.setTokenInfo(tokenInfo);
        Certificate result = repository.save(certificate);

        try {
            userCreator.CreateUser(input.getOwnerId(), input.getOwnerId(), input.getOwnerId());
        } catch (Exception ignored) {
            log.error("Create user error" + input.getOwnerId(), ignored);
        }
        return result;
    }

    private String getAlias(String inputAlias, P12CryptoToken cryptoToken) throws CryptoTokenException {
        if (inputAlias != null && !inputAlias.isEmpty())
            return inputAlias;
        List<String> aliases = cryptoToken.getAliases();
        if (aliases != null && aliases.size() > 0)
            return aliases.get(0);
        throw new CryptoTokenException("Can not found alias in crypto token");
    }
}
