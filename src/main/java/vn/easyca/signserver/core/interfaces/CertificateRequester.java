package vn.easyca.signserver.core.interfaces;

import vn.easyca.signserver.core.domain.CertPackage;
import vn.easyca.signserver.core.domain.OwnerInfo;
import vn.easyca.signserver.core.domain.RawCertificate;
import vn.easyca.signserver.core.domain.SubjectDN;

public interface CertificateRequester {

    class CertificateRequesterException extends Exception {
        public CertificateRequesterException(Throwable cause) {
            super(cause);
        }
    }

    RawCertificate request(String csr, CertPackage certPackage, SubjectDN subjectDN, OwnerInfo ownerInfo) throws CertificateRequesterException;
}
