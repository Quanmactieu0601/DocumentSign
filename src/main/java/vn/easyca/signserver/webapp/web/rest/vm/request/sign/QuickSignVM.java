package vn.easyca.signserver.webapp.web.rest.vm.request.sign;
import vn.easyca.signserver.core.dto.CertificateGenerateDTO;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningContainerRequest;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningRequest;
import vn.easyca.signserver.webapp.web.rest.vm.request.CertificateGeneratorVM;
import java.util.List;

public class QuickSignVM extends CertificateGeneratorVM {
    SigningRequest<SigningContainerRequest<Object, String>> signingElement;

    public SigningRequest<SigningContainerRequest<Object, String>> getSigningElement() { return signingElement; }

    public void setSigningElement(SigningRequest<SigningContainerRequest<Object, String>> signingElement) { this.signingElement = signingElement; }
}
