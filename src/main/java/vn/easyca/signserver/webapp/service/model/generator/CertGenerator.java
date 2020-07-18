package vn.easyca.signserver.webapp.service.model.generator;

import com.google.gson.Gson;
import org.json.JSONObject;
import vn.easyca.signserver.ca.service.api.RegisterCertificateApi;
import vn.easyca.signserver.ca.service.api.dto.RegisterInputDto;
import vn.easyca.signserver.ca.service.api.dto.RegisterResultDto;
import vn.easyca.signserver.core.cryptotoken.CryptoToken;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class CertGenerator {
    public static final int CERT_TYPE  =2;
    public static final String CERT_METHOD ="SOFT_TOKEN";
    public static final String OU = "IT";

    private CryptoToken cryptoToken;
    private RegisterCertificateApi registerCertificateApi;

    public CertGenerator(CryptoToken cryptoToken,  RegisterCertificateApi registerCertificateApi) {
        this.cryptoToken = cryptoToken;
        this.registerCertificateApi = registerCertificateApi;
    }

    public CertGeneratorOutput genCert(CertGeneratorInput input) throws Exception {
        KeyPair keyPair = cryptoToken.genKeyPair(input.getAlias(), input.getKeyLength());
//        PublicKey publicKey = cryptoToken.getPublicKey(input.getAlias());
//        PrivateKey privateKey = cryptoToken.getPrivateKey(input.getAlias());
        CSRGenerator csrGenerator = new CSRGenerator();
        String csr = csrGenerator.generatePKCS10(
            keyPair.getPublic(),
            keyPair.getPrivate() ,
            input.getCN(),
            input.getOU(),
            input.getO(),
            input.getL(),
            input.getS(),
            input.getC());
        RegisterInputDto registerInputDto = new RegisterInputDto();
        registerInputDto.setCsr(csr);
        registerInputDto.setCertMethod(CERT_METHOD);
        registerInputDto.setCertProfile(input.getCertProfile());
        registerInputDto.setCertProfileType(CERT_TYPE);
        registerInputDto.setCn(input.getCN());
        registerInputDto.setCustomerEmail(input.getOwnerEmail());
        registerInputDto.setCustomerPhone(input.getOwnerPhone());
        registerInputDto.setId(input.getOwnerId());
        registerInputDto.setO(input.getO());
        registerInputDto.setOu(OU);
        registerInputDto.setSt(input.getS());
        Gson gson = new Gson();
        String json = gson.toJson(registerInputDto);
        RegisterResultDto registerResultDto = registerCertificateApi.register(registerInputDto);
        return new CertGeneratorOutput(registerResultDto.getCert(),registerResultDto.getCertSerial());
    }
}
