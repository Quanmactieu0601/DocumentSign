package vn.easyca.signserver.application.dependency;

import vn.easyca.signserver.application.domain.CertPackage;
import vn.easyca.signserver.application.domain.OwnerInfo;
import vn.easyca.signserver.application.domain.RawCertificate;
import vn.easyca.signserver.application.domain.SubjectDN;

public interface CertificateRequester {

    public class CertificateRequesterException extends Exception {

        public CertificateRequesterException(Throwable cause) {
            super(cause);
        }
    }

    RawCertificate request(String csr, CertPackage certPackage, SubjectDN subjectDN, OwnerInfo ownerInfo) throws CertificateRequesterException;
}
