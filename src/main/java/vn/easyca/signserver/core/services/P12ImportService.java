package vn.easyca.signserver.core.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.sign.core.cryptotoken.Config;
import vn.easyca.signserver.sign.core.cryptotoken.P12CryptoToken;
import vn.easyca.signserver.core.domain.Certificate;
import vn.easyca.signserver.core.domain.TokenInfo;
import vn.easyca.signserver.core.repository.CertificateRepository;
import vn.easyca.signserver.core.utils.CommonUtils;
import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;
@Service
public class P12ImportService {

    @Autowired
    CertificateRepository repository;

    public Certificate Import(P12ImportFileInput input) throws Exception {
        P12CryptoToken p12CryptoToken = new P12CryptoToken();
        Config config = new Config();
        byte[] fileContent = Base64.getDecoder().decode(input.getP12Base64());
        config.initPkcs12(new ByteArrayInputStream(fileContent), input.getPin());
        try {
            p12CryptoToken.init(config);
            String alias = getAlias(input.getAliasName(),p12CryptoToken);
            X509Certificate x509Certificate = (X509Certificate) p12CryptoToken.getCertificate(alias);
            String serial = x509Certificate.getSerialNumber().toString(16);
            String base64Cert = CommonUtils.encodeBase64X509(x509Certificate);

            Certificate certificate =  new Certificate();
            certificate.setRawData(base64Cert);
            certificate.setOwnerId(input.getOwnerId());
            certificate.setSerial(serial);
            certificate.setAlias(alias);
            certificate.setTokenType(Certificate.PKCS_12);
            TokenInfo tokenInfo = new TokenInfo();
            tokenInfo.setData(input.getP12Base64());
            certificate.setTokenInfo(tokenInfo);
            return repository.save(certificate);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Could not import p12 certificate");
        }
    }

    private String getAlias(String inputAlias,P12CryptoToken cryptoToken) throws Exception {
        if (inputAlias!= null && !inputAlias.isEmpty())
            return inputAlias;
        List<String> aliases = cryptoToken.getAliases();
        if (aliases!=null&&aliases.size() >0)
            return aliases.get(0);
        throw new Exception("Can not found alias in crypto token");
    }

    public static class P12ImportFileInput {

        private String p12Base64;

        private String ownerId;

        private String serial;

        private String pin;

        private int keyLen;

        private String aliasName;

        public String getP12Base64() {
            return p12Base64;
        }

        public void setP12Base64(String p12Base64) {
            this.p12Base64 = p12Base64;
        }

        public String getOwnerId() {
            return ownerId;
        }

        public void setOwnerId(String ownerId) {
            this.ownerId = ownerId;
        }

        public String getSerial() {
            return serial;
        }

        public void setSerial(String serial) {
            this.serial = serial;
        }

        public String getPin() {
            return pin;
        }

        public void setPin(String pin) {
            this.pin = pin;
        }

        public int getKeyLen() {
            return keyLen;
        }

        public void setKeyLen(int keyLen) {
            this.keyLen = keyLen;
        }

        public String getAliasName() {
            return aliasName;
        }

        public void setAliasName(String aliasName) {
            this.aliasName = aliasName;
        }
    }


}
