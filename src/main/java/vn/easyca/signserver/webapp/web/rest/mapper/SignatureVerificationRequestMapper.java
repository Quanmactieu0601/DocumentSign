package vn.easyca.signserver.webapp.web.rest.mapper;

import org.modelmapper.ModelMapper;
import vn.easyca.signserver.business.services.sign.dto.request.SignatureVerificationRequest;
import vn.easyca.signserver.webapp.web.rest.vm.request.SignatureVerificationVM;

public class SignatureVerificationRequestMapper {

    public SignatureVerificationRequest map(SignatureVerificationVM vm) {
        ModelMapper mapper = new ModelMapper();
        SignatureVerificationRequest result = new SignatureVerificationRequest();
        result.setHashAlgorithm(vm.getHashAlgorithm());
        result.setSerial(vm.getSerial());
        for (SignatureVerificationVM.ElementVM elementVM : vm.getElements()) {
            result.add(mapper.map(elementVM, SignatureVerificationRequest.Element.class));
        }
        return result;
    }
}
