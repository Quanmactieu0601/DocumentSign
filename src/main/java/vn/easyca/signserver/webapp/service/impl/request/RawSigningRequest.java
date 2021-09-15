package vn.easyca.signserver.webapp.service.impl.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.easyca.signserver.core.dto.OptionalDTO;
import vn.easyca.signserver.core.dto.sign.TokenInfoDTO;
import vn.easyca.signserver.core.dto.sign.request.SignRequest;
import vn.easyca.signserver.core.dto.sign.response.SignDataResponse;
import vn.easyca.signserver.core.dto.sign.response.SignResultElement;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.services.SigningService;
import vn.easyca.signserver.webapp.service.SigningWrapRequestHandle;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.SigningVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.TokenVM;
import vn.easyca.signserver.webapp.web.rest.vm.response.SigningResult;

import java.util.List;

@Service
@Transactional
public class RawSigningRequest implements SigningWrapRequestHandle {
    private final int RESULT_OK = 0;
    private final int RESULT_ERROR = 1;

    private final SigningService signingService;

    public RawSigningRequest(SigningService signingService) {
        this.signingService = signingService;
    }

    @Override
    public SigningResult sign(Object requestValue, TokenInfoDTO tokenInfo, OptionalDTO optional, String key) throws ApplicationException {
        SigningVM<String> signingVM = mapper.convertValue(requestValue, SigningVM.class);
        TokenVM tokenVM = new TokenVM();
        tokenVM.setSerial(tokenInfo.getSerial());
        tokenVM.setPin(tokenInfo.getPin());
        signingVM.setTokenInfo(tokenVM);
        signingVM.setOptional(optional);
        SignRequest<String> signingRawRequest = signingVM.getDTO(String.class);
        SignDataResponse<List<SignResultElement>> signResponse = signingService.signRaw(signingRawRequest);
        String signedContent = signResponse.getSignResult().get(0).getBase64Signature();
        SigningResult result = new SigningResult(signedContent, key, "Ký tệp thành công", RESULT_OK);
        return result;
    }
}
