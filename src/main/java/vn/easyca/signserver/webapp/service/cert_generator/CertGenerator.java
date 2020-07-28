package vn.easyca.signserver.webapp.service.cert_generator;

import vn.easyca.signserver.core.cryptotoken.CryptoToken;
import vn.easyca.signserver.core.cryptotoken.utils.CertRequestUtils;
import vn.easyca.signserver.webapp.service.domain.RawCertificate;
import vn.easyca.signserver.webapp.service.port.CertificateRequester;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class CertGenerator {

    private final CryptoToken cryptoToken;

    private final CertificateRequester requester;

    private String alias;

    private int keyLength;

    private SubjectDN subjectDN;

    private OwnerInfo ownerInfo;

    private CertPackage certPackage;

    public CertGenerator(CryptoToken cryptoToken, CertificateRequester requester) {
        this.cryptoToken = cryptoToken;
        this.requester = requester;
    }

    public CertGenerator setAlias(String alias) {
        this.alias = alias;
        return this;
    }


    public CertGenerator setKeyLength(int keyLength) {
        this.keyLength = keyLength;
        return this;

    }

    public CertGenerator setSubjectDN(SubjectDN subjectDN) {
        this.subjectDN = subjectDN;
        return this;

    }

    public CertGenerator setOwnerInfo(OwnerInfo ownerInfo) {
        this.ownerInfo = ownerInfo;
        return this;

    }

    public CertGenerator setCertPackage(CertPackage certPackage) {
        this.certPackage = certPackage;
        return this;
    }

    public RawCertificate genCert() throws Exception {
        KeyPair keyPair = genKeyPair(alias, keyLength);
        String csr = genCsr(keyPair.getPublic(), keyPair.getPrivate(), subjectDN);
        return requester.request(csr, certPackage, subjectDN, ownerInfo);
    }

    private KeyPair genKeyPair(String alias, int keyLength) throws Exception {
        return cryptoToken.genKeyPair(alias, keyLength);
    }

    private String genCsr(PublicKey publicKey, PrivateKey privateKey, SubjectDN subjectDN) throws Exception {
        CertRequestUtils certRequestUtils = new CertRequestUtils();
        return certRequestUtils.genCsr(subjectDN.toString(),
            cryptoToken.getProviderName(),
            privateKey,
            publicKey,
            null,
            false,
            false);
    }
}
