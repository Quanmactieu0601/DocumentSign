//package vn.easyca.signserver.webapp.service.model;
//
//import vn.easyca.signserver.core.cryptotoken.CryptoToken;
//import vn.easyca.signserver.webapp.service.dto.CreateCertificateDto;
//
//public class CertificateCreator {
//    public class CreatedResult {
//    }
//
//    private CryptoToken cryptoToken;
//
//    public Certificate create(CreateCertificateDto dto) throws Exception {
//        String alias = genAlias(dto);
//        cryptoToken.genKeyPair(alias,dto.getKeyLen());
//        java.security.cert.Certificate certificate= cryptoToken.getCertificate(alias);
//        Certificate domainc
//        Certificate certificate = new Certificate();
//        certificate.setAlias(alias);
//        certificate.setOwnerId(dto.getOwnerId());
//        certificate.setSerial();
//    }
//
//    private String genAlias(CreateCertificateDto dto) {
//        dto.getOwnerId() + ":" + dto.getCn()
//    }
//
//}
