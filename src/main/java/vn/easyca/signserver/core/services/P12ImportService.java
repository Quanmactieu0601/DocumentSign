package vn.easyca.signserver.core.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.domain.CertificateDTO;
import vn.easyca.signserver.core.dto.ImportP12FileDTO;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.exception.CertificateAppException;
import vn.easyca.signserver.pki.cryptotoken.P12CryptoToken;
import vn.easyca.signserver.core.domain.TokenInfo;
import vn.easyca.signserver.core.utils.CommonUtils;
import vn.easyca.signserver.pki.cryptotoken.error.*;
import vn.easyca.signserver.webapp.config.ApplicationProperties;
import vn.easyca.signserver.webapp.config.SystemDbConfiguration;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.repository.CertificateRepository;
import vn.easyca.signserver.webapp.service.CertificateService;
import vn.easyca.signserver.webapp.service.SystemConfigCachingService;
import vn.easyca.signserver.webapp.service.UserApplicationService;
import vn.easyca.signserver.webapp.utils.DateTimeUtils;
import vn.easyca.signserver.webapp.utils.SymmetricEncryptors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

@Service
public class P12ImportService {
    private final Log log = LogFactory.getLog(P12ImportService.class);

    private final CertificateService certificateService;
    private final UserApplicationService userApplicationService;
    private final SymmetricEncryptors symmetricService;
    private final CertificateRepository certificateRepository;
    private final SystemConfigCachingService systemConfigCachingService;

    @Autowired
    public P12ImportService(CertificateService certificateService, UserApplicationService userApplicationService,
                            SymmetricEncryptors symmetricService, CertificateRepository certificateRepository, SystemConfigCachingService systemConfigCachingService) {
        this.certificateService = certificateService;
        this.userApplicationService = userApplicationService;
        this.symmetricService = symmetricService;
        this.certificateRepository = certificateRepository;
        this.systemConfigCachingService = systemConfigCachingService;
    }

    public CertificateDTO insert(ImportP12FileDTO input) throws ApplicationException {
        SystemDbConfiguration dbConfiguration = systemConfigCachingService.getConfig();
        P12CryptoToken p12CryptoToken = new P12CryptoToken();
        try {
            p12CryptoToken.initPkcs12(input.getP12Base64(), input.getPin());
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
        Optional<Certificate> certBySerial = certificateRepository.findOneBySerial(serial);
        if (certBySerial.isPresent())
            throw new ApplicationException(-1, "Certificate is already exist");
        String base64Cert = null;
        try {
            base64Cert = CommonUtils.encodeBase64X509(x509Certificate);
        } catch (CertificateEncodingException e) {
            throw new CertificateAppException("certificate encoding exception", e);
        }
        CertificateDTO certificateDTO = new CertificateDTO();
        certificateDTO.setRawData(base64Cert);
        certificateDTO.setOwnerId(input.getOwnerId());
        certificateDTO.setSerial(serial);
        certificateDTO.setAlias(alias);
        certificateDTO.setTokenType(CertificateDTO.PKCS_12);
        // Lưu thêm mã pin khi tạo cert
        if (dbConfiguration.getSaveTokenPassword())
            certificateDTO.setEncryptedPin(symmetricService.encrypt(input.getPin()));
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setData(input.getP12Base64());
        certificateDTO.setTokenInfo(tokenInfo);
        certificateDTO.setValidDate(DateTimeUtils.convertToLocalDateTime(x509Certificate.getNotBefore()));
        certificateDTO.setExpiredDate(DateTimeUtils.convertToLocalDateTime(x509Certificate.getNotAfter()));
        certificateDTO.setActiveStatus(1);

        CertificateDTO result = certificateService.save(certificateDTO);

        try {
            userApplicationService.createUser(input.getOwnerId(), input.getOwnerId(), input.getOwnerId());
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

    // TODO: Call this function to change P12 password
    public byte[] changePKCS12KeyPassword(byte[] privateKeyData, String oldPassword, String newPassword) {
        try {
            KeyStore newKs = KeyStore.getInstance("PKCS12");
            newKs.load(null, null);

            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(new ByteArrayInputStream(privateKeyData), oldPassword.toCharArray());
            Enumeration<String> aliases = ks.aliases();

            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                Key privateKey = ks.getKey(alias, oldPassword.toCharArray());
                java.security.cert.Certificate[] certificateChain = ks.getCertificateChain(alias);
                newKs.setKeyEntry(alias, privateKey, newPassword.toCharArray(), certificateChain);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            newKs.store(baos, newPassword.toCharArray());
            return baos.toByteArray();
        } catch (KeyStoreException
            | CertificateException
            | NoSuchAlgorithmException
            | UnrecoverableKeyException
            | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
