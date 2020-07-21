package vn.easyca.signserver.webapp.service.model.cert.generator;

import vn.easyca.signserver.ca.service.api.RegisterCertificateApi;
import vn.easyca.signserver.ca.service.api.dto.RegisterInputDto;
import vn.easyca.signserver.ca.service.api.dto.RegisterResultDto;
import vn.easyca.signserver.ca.service.network.Unauthorized;
import vn.easyca.signserver.core.cryptotoken.CryptoToken;
import vn.easyca.signserver.webapp.service.model.cert.data.*;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class CertGenerator {

    private CryptoToken cryptoToken;
    private RegisterCertificateApi registerCertificateApi;

    public CertGenerator(CryptoToken cryptoToken, RegisterCertificateApi registerCertificateApi) {
        this.cryptoToken = cryptoToken;
        this.registerCertificateApi = registerCertificateApi;
    }

    public CertGeneratorOutput genCert(CertGeneratorInput input) throws Exception {

        KeyPair keyPair = genKeyPair(input.getAlias(), input.getKeyLength());
        String csr = genCsr(keyPair.getPublic(),keyPair.getPrivate(),input.getCertProfile());
        return requestCertToCA(csr,input.getServiceInfo(), input.getCertProfile(),input.getOwnerInfo());
    }

    private KeyPair genKeyPair(String alias, int keyLength) throws Exception {
        return cryptoToken.genKeyPair(alias, keyLength);
    }

    private String genCsr(PublicKey publicKey, PrivateKey privateKey, SubjectInfo certProfile) throws Exception {
        CSRGenerator csrGenerator = new CSRGenerator();
        return csrGenerator.generatePKCS10(publicKey,privateKey, certProfile);
    }

    private CertGeneratorOutput requestCertToCA(String csr, ServiceInfo serviceInfo, SubjectInfo certProfile, OwnerInfo ownerInfo) throws IOException, Unauthorized {

        RegisterInputDto registerInputDto = new RegisterInputDto();
        registerInputDto.setCsr(csr);
        registerInputDto.setCertMethod(serviceInfo.getCertMethod());
        registerInputDto.setCertProfile(serviceInfo.getCertProfile());
        registerInputDto.setCertProfileType(serviceInfo.getCertProfileType());
        registerInputDto.setCn(certProfile.getCn());
        registerInputDto.setCustomerEmail(ownerInfo.getOwnerEmail());
        registerInputDto.setCustomerPhone(ownerInfo.getOwnerPhone());
        registerInputDto.setId(ownerInfo.getOwnerId());
        registerInputDto.setO(certProfile.getO());
        registerInputDto.setOu(certProfile.getOu());
        registerInputDto.setSt(certProfile.getS());
        RegisterResultDto registerResultDto = registerCertificateApi.register(registerInputDto);
        return new CertGeneratorOutput(registerResultDto.getCert(), registerResultDto.getCertSerial());
    }
}
