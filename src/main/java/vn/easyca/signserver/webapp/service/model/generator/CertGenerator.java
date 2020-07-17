package vn.easyca.signserver.webapp.service.model.generator;

import vn.easyca.signserver.ca.service.api.RegisterCertificateApi;
import vn.easyca.signserver.ca.service.api.dto.RegisterInputDto;
import vn.easyca.signserver.ca.service.api.dto.RegisterResultDto;
import vn.easyca.signserver.core.cryptotoken.CryptoToken;

import java.security.PrivateKey;
import java.security.PublicKey;

public class CertGenerator {


    private CryptoToken cryptoToken;

    private RegisterCertificateApi registerCertificateApi;

    public CertGenerator(CryptoToken cryptoToken,  RegisterCertificateApi registerCertificateApi) {
        this.cryptoToken = cryptoToken;
        this.registerCertificateApi = registerCertificateApi;
    }

    public CertGeneratorOutput genCert(CertGeneratorInput input) throws Exception {

        cryptoToken.genKeyPair(input.getAlias(), input.getKeyLength());
        PublicKey publicKey = cryptoToken.getPublicKey(input.getAlias());
        PrivateKey privateKey = cryptoToken.getPrivateKey(input.getAlias());
        CSRGenerator csrGenerator = new CSRGenerator();
        String csr = csrGenerator.generatePKCS10(
            publicKey,
            privateKey,
            input.getCN(),
            input.getOU(),
            input.getO(),
            input.getL(),
            input.getS(),
            input.getC());
        RegisterInputDto registerInputDto = new RegisterInputDto();
        registerInputDto.setCsr(csr);
        registerInputDto.setCertMethod("SOFT_TOKEN");
        registerInputDto.setCertProfile(input.getCertProfile());
        registerInputDto.setCertProfileType(input.getCertType());
        registerInputDto.setCn(input.getCN());
        registerInputDto.setCustomerEmail("emailrac89@gmail.com");
        registerInputDto.setId("173846902");
        registerInputDto.setOu("IT");
        registerInputDto.setSt("1");
        RegisterResultDto registerResultDto = registerCertificateApi.register(registerInputDto);
        return new CertGeneratorOutput(registerResultDto.getCert(),registerResultDto.getCertSerial());
    }
}
