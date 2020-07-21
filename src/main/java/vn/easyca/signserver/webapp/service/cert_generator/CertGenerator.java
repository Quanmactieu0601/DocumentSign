package vn.easyca.signserver.webapp.service.cert_generator;

import vn.easyca.signserver.ca.service.api.RegisterCertificateApi;
import vn.easyca.signserver.ca.service.api.dto.RegisterInputDto;
import vn.easyca.signserver.ca.service.api.dto.RegisterResultDto;
import vn.easyca.signserver.ca.service.network.RAUnauthorized;
import vn.easyca.signserver.core.cryptotoken.P11CryptoToken;
import vn.easyca.signserver.core.cryptotoken.utils.CertRequestUtils;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class CertGenerator {

    private P11CryptoToken cryptoToken;
    private RegisterCertificateApi registerCertificateApi;

    public CertGenerator(P11CryptoToken cryptoToken, RegisterCertificateApi registerCertificateApi) {
        this.cryptoToken = cryptoToken;
        this.registerCertificateApi = registerCertificateApi;
    }

    public CertGeneratorOutput genCert(CertGeneratorInput input) throws Exception {
        KeyPair keyPair = genKeyPair(input.getAlias(), input.getKeyLength());
        String csr = genCsr(keyPair.getPublic(), keyPair.getPrivate(), input.getSubjectDN());
        return requestCertToCA(csr, input.getCertPackage(), input.getSubjectDN(), input.getOwnerInfo());
    }

    private KeyPair genKeyPair(String alias, int keyLength) throws Exception {
        return cryptoToken.genKeyPair(alias, keyLength);
    }

    private String genCsr(PublicKey publicKey, PrivateKey privateKey, SubjectDN subjectDN) throws Exception {
        CertRequestUtils certRequestUtils = new CertRequestUtils();
        return certRequestUtils.genCsr(subjectDN.toString(), cryptoToken.getProviderName(),
            privateKey, publicKey, null, false, false);
    }

    private CertGeneratorOutput requestCertToCA(String csr, CertPackage certPackage,
                                                SubjectDN subjectDN, OwnerInfo ownerInfo) throws IOException, RAUnauthorized {
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
        RegisterResultDto registerResultDto = registerCertificateApi.register(registerInputDto);
        return new CertGeneratorOutput(registerResultDto.getCert(), registerResultDto.getCertSerial());
    }
}
