package vn.easyca.signserver.application.dependency;

import vn.easyca.signserver.application.domain.CertPackage;
import vn.easyca.signserver.application.domain.OwnerInfo;
import vn.easyca.signserver.application.domain.RawCertificate;
import vn.easyca.signserver.application.domain.SubjectDN;

public interface CertificateRequester {
    RawCertificate request(String csr, CertPackage certPackage, SubjectDN subjectDN, OwnerInfo ownerInfo) throws Exception;
}
