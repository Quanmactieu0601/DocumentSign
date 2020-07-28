package vn.easyca.signserver.webapp.service.port;
import vn.easyca.signserver.webapp.service.cert_generator.CertPackage;
import vn.easyca.signserver.webapp.service.cert_generator.OwnerInfo;
import vn.easyca.signserver.webapp.service.cert_generator.SubjectDN;
import vn.easyca.signserver.webapp.service.domain.RawCertificate;

public interface CertificateRequester {

    RawCertificate request(String csr, CertPackage certPackage, SubjectDN subjectDN, OwnerInfo ownerInfo) throws Exception;
}
