package vn.easyca.signserver.webapp.service.impl.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.easyca.signserver.core.dto.OptionalDTO;
import vn.easyca.signserver.core.dto.sign.TokenInfoDTO;
import vn.easyca.signserver.core.dto.sign.request.SignRequest;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.services.SigningService;
import vn.easyca.signserver.webapp.service.SigningWrapRequestHandle;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.SigningVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.TokenVM;
@Service
@Transactional
public class HashSigningRequest implements SigningWrapRequestHandle {
    private final SigningService signingService;
    public HashSigningRequest(SigningService signingService) {
        this.signingService = signingService;
    }

    @Override
    public Object sign(Object requestValue, TokenInfoDTO tokenInfo, OptionalDTO optional) throws ApplicationException {
        SigningVM<String> signingVM = mapper.convertValue(requestValue, SigningVM.class);
        TokenVM tokenVM = new TokenVM();
        tokenVM.setSerial(tokenInfo.getSerial());
        tokenVM.setPin(tokenInfo.getPin());
        signingVM.setTokenInfo(tokenVM);
        signingVM.setOptional(optional);
        SignRequest<String> signHashRequest = signingVM.getDTO(String.class);
        Object res = signingService.signHash(signHashRequest, false);
        return res;
    }
}
