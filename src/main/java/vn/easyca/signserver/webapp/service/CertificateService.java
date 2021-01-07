package vn.easyca.signserver.webapp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.easyca.signserver.core.domain.CertificateDTO;
import vn.easyca.signserver.core.domain.TokenInfo;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.exception.CertificateAppException;
import vn.easyca.signserver.core.exception.CertificateNotFoundAppException;
import vn.easyca.signserver.core.factory.CryptoTokenProxy;
import vn.easyca.signserver.core.factory.CryptoTokenProxyFactory;
import vn.easyca.signserver.core.utils.CertUtils;
import vn.easyca.signserver.webapp.domain.*;
import vn.easyca.signserver.webapp.domain.Certificate;
import vn.easyca.signserver.webapp.repository.CertificateRepository;
import vn.easyca.signserver.webapp.repository.SignatureImageRepository;
import vn.easyca.signserver.webapp.repository.SignatureTemplateRepository;
import vn.easyca.signserver.webapp.repository.UserRepository;
import vn.easyca.signserver.webapp.security.AuthenticatorTOTPService;
import vn.easyca.signserver.webapp.service.mapper.CertificateMapper;
import vn.easyca.signserver.webapp.utils.AccountUtils;
import vn.easyca.signserver.webapp.utils.CertificateEncryptionHelper;
import vn.easyca.signserver.webapp.utils.FileIOHelper;
import vn.easyca.signserver.webapp.utils.ParserUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

@Service
public class CertificateService {

    private Optional<UserEntity> userEntityOptional;
    private List<Certificate> certificateList = new ArrayList<>();
    private final CertificateRepository certificateRepository;
    private final CertificateMapper mapper;
    private final SignatureTemplateRepository signatureTemplateRepository;
    private final SignatureImageRepository signatureImageRepository;
    private final CryptoTokenProxyFactory cryptoTokenProxyFactory;
    private final AuthenticatorTOTPService authenticatorTOTPService;

    private final UserRepository userRepository;

    public CertificateService(CertificateRepository certificateRepository, CertificateEncryptionHelper encryptionHelper, CertificateMapper mapper, SignatureTemplateRepository signatureTemplateRepository, SignatureImageRepository signatureImageRepository, CryptoTokenProxyFactory cryptoTokenProxyFactory, AuthenticatorTOTPService authenticatorTOTPService, UserRepository userRepository) {
        this.certificateRepository = certificateRepository;
        this.mapper = mapper;
        this.signatureTemplateRepository = signatureTemplateRepository;
        this.signatureImageRepository = signatureImageRepository;
        this.cryptoTokenProxyFactory = cryptoTokenProxyFactory;
        this.authenticatorTOTPService = authenticatorTOTPService;
        this.userRepository = userRepository;
    }

    public List<Certificate> getByOwnerId(String ownerId) {
        Long id = Long.parseLong(ownerId);
        userEntityOptional = userRepository.findById(id);
        System.out.println(userEntityOptional);
        UserEntity userEntity = userEntityOptional.get();
        boolean roleAdmin = false;
        Set<Authority> userAuthority = userEntity.getAuthorities();
        for (Authority setAuthority : userAuthority) {
            if (setAuthority.getName().equals("ROLE_ADMIN")) {
                roleAdmin = true;
            }
        }
        if (roleAdmin) {
            certificateList = certificateRepository.findAll();
        } else {
            certificateList = certificateRepository.findByOwnerId(ownerId);
        }
        return certificateList;
    }

    public CertificateDTO getBySerial(String serial) throws CertificateNotFoundAppException {
        Optional<Certificate> certificate = certificateRepository.findOneBySerial(serial);
        CertificateDTO certificateDTO = null;
        if (certificate.isPresent()) {
            certificateDTO = mapper.map(certificate.get());
        }
        if (certificateDTO == null)
            throw new CertificateNotFoundAppException();
        return certificateDTO;
    }

    @Transactional(readOnly = true)
    public Page<Certificate> findAll(Pageable pageable) {
        return certificateRepository.findAll(pageable);
    }

    @Transactional
    public void updateActiveStatus(long id) {
        Certificate certificate = certificateRepository.getOne(id);
        if (certificate.getActiveStatus() == 1) {
            certificate.setActiveStatus(0);
        } else {
            certificate.setActiveStatus(1);
        }
        certificateRepository.save(certificate);
    }

    @Transactional(readOnly = true)
    public Page<Certificate> findByFilter(Pageable pageable, String alias, String ownerId, String serial, String validDate, String expiredDate) {
        return certificateRepository.findByFilter(pageable, alias, ownerId, serial, validDate, expiredDate);
    }

    @Transactional
    public CertificateDTO save(CertificateDTO certificateDTO) {
        Certificate entity = mapper.map(certificateDTO);
        certificateRepository.save(entity);
        return mapper.map(entity);
    }

    public String getSignatureImage(String serial, String pin) throws ApplicationException {
        Optional<Certificate> certificateOptional = certificateRepository.findOneBySerial(serial);
        if (!certificateOptional.isPresent())
            throw new ApplicationException("Certificate is not found");
        CertificateDTO certificateDTO = mapper.map(certificateOptional.get());
        CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(certificateDTO, pin);
        if (!cryptoTokenProxy.getCryptoToken().isInitialized()) {
            throw new ApplicationException("Cannot init token");
        }
        Optional<UserEntity> userEntity = userRepository.findOneWithAuthoritiesByLogin(AccountUtils.getLoggedAccount());
        Optional<SignatureTemplate> signatureTemplateDTO = signatureTemplateRepository.findOneByUserId(userEntity.get().getId());
        if (!signatureTemplateDTO.isPresent()) {
            throw new ApplicationException("Signature template is not configured");
        }

        Long signImageId = certificateDTO.getSignatureImageId();
        String signatureImageData = "";
        String signatureTemplate = signatureTemplateDTO.get().getHtmlTemplate();
        if (signImageId != null) {
            Optional<SignatureImage> signatureImage = signatureImageRepository.findById(signImageId);
            if (signatureImage.isPresent())
                signatureImageData = signatureImage.get().getImgData();
        }
        X509Certificate x509Certificate = cryptoTokenProxy.getX509Certificate();
        String subjectDN = x509Certificate.getSubjectDN().getName();

        String htmlContent = ParserUtils.getHtmlTemplateAndSignData(subjectDN, signatureTemplate, signatureImageData);
        return ParserUtils.convertHtmlContentToBase64(htmlContent);
    }

    public Optional<Certificate> findOne(Long id) {
        return certificateRepository.findById(id);
    }

    @Transactional
    public void saveOrUpdate(Certificate certificate) {
        certificateRepository.save(certificate);
    }

    public String getBase64OTPQRCode(String serial, String pin) throws ApplicationException {
        Optional<Certificate> certificateOptional = certificateRepository.findOneBySerial(serial);
        if (!certificateOptional.isPresent())
            throw new ApplicationException(String.format("Certificate is not exist - serial: %s", serial));
        CertificateDTO certificateDTO = mapper.map(certificateOptional.get());
        CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(certificateDTO, pin);
        if (!cryptoTokenProxy.getCryptoToken().isInitialized()) {
            throw new ApplicationException("Pin is not correct");
        }
        String urlQrCode = authenticatorTOTPService.getQRCodeFromEncryptedSecretKey(serial, certificateOptional.get().getSecretKey());
        try {
            return FileIOHelper.getBase64EncodedImage(urlQrCode);
        } catch (IOException e) {
            throw new ApplicationException("Can't convert URL to base64 image", e);
        }
    }

    // TODO: Call this function to change P12 password
    public void changePIN(String serial, String oldPin, String newPin, String otp) throws ApplicationException {
        try {
            Optional<Certificate> certificateOptional = certificateRepository.findOneBySerial(serial);
            if (!certificateOptional.isPresent())
                throw new ApplicationException("Certificate is not found");
            CertificateDTO certificateDTO = mapper.map(certificateOptional.get());
            CryptoTokenProxy cryptoTokenProxy = cryptoTokenProxyFactory.resolveCryptoTokenProxy(certificateDTO, oldPin, otp);
            if (!cryptoTokenProxy.getCryptoToken().isInitialized()) {
                throw new ApplicationException("Cannot init token");
            }
            if (certificateDTO.getTokenType() == CertificateDTO.PKCS_11) {
                // TODO: change PIN HSM
            } else {
                KeyStore newKs = KeyStore.getInstance("PKCS12");
                newKs.load(null, null);

                KeyStore currentKs = cryptoTokenProxy.getCryptoToken().getKeyStore();
                Enumeration<String> aliases = currentKs.aliases();

                while (aliases.hasMoreElements()) {
                    String alias = aliases.nextElement();
                    Key privateKey = currentKs.getKey(alias, oldPin.toCharArray());
                    java.security.cert.Certificate[] certificateChain = currentKs.getCertificateChain(alias);
                    newKs.setKeyEntry(alias, privateKey, newPin.toCharArray(), certificateChain);
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                newKs.store(baos, newPin.toCharArray());
                byte[] output = baos.toByteArray();
                X509Certificate x509Certificate = (X509Certificate) newKs.getCertificate(certificateDTO.getAlias());

                String base64Cert = CertUtils.encodeBase64X509(x509Certificate);

                certificateDTO.setRawData(base64Cert);
                TokenInfo tokenInfo = new TokenInfo();
                tokenInfo.setData(Base64.getEncoder().encodeToString(output));
                certificateDTO.setTokenInfo(tokenInfo);
            }
            this.save(certificateDTO);
        } catch (IOException e) {
            throw new ApplicationException("IOException", e);
        } catch (NoSuchAlgorithmException e) {
            throw new ApplicationException("NoSuchAlgorithmException", e);
        } catch (UnrecoverableKeyException e) {
            throw new ApplicationException("UnrecoverableKeyException", e);
        } catch (CertificateEncodingException e) {
            throw new ApplicationException("CertificateEncodingException", e);
        } catch (KeyStoreException | CertificateException e) {
            throw new ApplicationException("KeyStoreException | CertificateException", e);
        }
    }
}
