package core.interfaces;

import core.domain.CertPackage;
import core.domain.OwnerInfo;
import core.domain.RawCertificate;
import core.domain.SubjectDN;
import java.util.List;
import ra.lib.dto.RegisterInputDto;
import ra.lib.dto.RegisterResultDto;

public interface CertificateRequester {
    class CertificateRequesterException extends Exception {

        public CertificateRequesterException(Throwable cause) {
            super(cause);
        }
    }

    RawCertificate request(String csr, CertPackage certPackage, SubjectDN subjectDN, OwnerInfo ownerInfo)
        throws CertificateRequesterException;

    List<RegisterResultDto> request(List<RegisterInputDto> certRequests) throws CertificateRequesterException;
}
