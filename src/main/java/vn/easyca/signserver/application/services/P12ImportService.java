package vn.easyca.signserver.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.application.dto.ImportP12FileDTO;
import vn.easyca.signserver.application.dependency.UserCreator;
import vn.easyca.signserver.pki.cryptotoken.Config;
import vn.easyca.signserver.pki.cryptotoken.P12CryptoToken;
import vn.easyca.signserver.application.domain.Certificate;
import vn.easyca.signserver.application.domain.TokenInfo;
import vn.easyca.signserver.application.repository.CertificateRepository;
import vn.easyca.signserver.application.utils.CommonUtils;

import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;

@Service
public class P12ImportService {

    @Autowired
    CertificateRepository repository;

    @Autowired
    UserCreator userCreator;

    public Certificate insert(ImportP12FileDTO input) throws Exception {
        P12CryptoToken p12CryptoToken = new P12CryptoToken();
        Config config = new Config();
        byte[] fileContent = Base64.getDecoder().decode(input.getP12Base64());
        config.initPkcs12(new ByteArrayInputStream(fileContent), input.getPin());
        Certificate result;
        try {
            p12CryptoToken.init(config);
            String alias = getAlias(input.getAliasName(), p12CryptoToken);
            X509Certificate x509Certificate = (X509Certificate) p12CryptoToken.getCertificate(alias);
            String serial = x509Certificate.getSerialNumber().toString(16);
            String base64Cert = CommonUtils.encodeBase64X509(x509Certificate);
            Certificate certificate = new Certificate();
            certificate.setRawData(base64Cert);
            certificate.setOwnerId(input.getOwnerId());
            certificate.setSerial(serial);
            certificate.setAlias(alias);
            certificate.setTokenType(Certificate.PKCS_12);
            TokenInfo tokenInfo = new TokenInfo();
            tokenInfo.setData(input.getP12Base64());
            certificate.setTokenInfo(tokenInfo);
            result = repository.save(certificate);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not import p12 certificate");
        }
        try {
            userCreator.CreateUser(input.getOwnerId(), input.getOwnerId(), input.getOwnerId());
        } catch (Exception ignored) {
        }
        return result;
    }

    private String getAlias(String inputAlias, P12CryptoToken cryptoToken) throws Exception {
        if (inputAlias != null && !inputAlias.isEmpty())
            return inputAlias;
        List<String> aliases = cryptoToken.getAliases();
        if (aliases != null && aliases.size() > 0)
            return aliases.get(0);
        throw new Exception("Can not found alias in crypto token");
    }
}
