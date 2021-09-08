package vn.easyca.signserver.webapp.service.impl.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.easyca.signserver.core.dto.OptionalDTO;
import vn.easyca.signserver.core.dto.sign.TokenInfoDTO;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningRequest;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningRequestContent;
import vn.easyca.signserver.core.dto.sign.newresponse.SigningResponse;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.services.OfficeSigningService;
import vn.easyca.signserver.webapp.service.SigningWrapRequestHandle;
import vn.easyca.signserver.webapp.web.rest.vm.response.BaseResponseVM;

import java.util.ArrayList;
import java.util.List;
@Service
@Transactional
public class OfficeSigningRequest implements SigningWrapRequestHandle {
    private final OfficeSigningService officeSigningService;
    public OfficeSigningRequest(OfficeSigningService officeSigningService) {
        this.officeSigningService = officeSigningService;
    }

    @Override
    public Object sign(Object requestValue, TokenInfoDTO tokenInfo, OptionalDTO optional) throws Exception {
        SigningRequestContent signingRequestConvert = mapper.convertValue(requestValue, SigningRequestContent.class);
        List<SigningRequestContent> lstTemp = new ArrayList<>();
        lstTemp.add(signingRequestConvert);
        SigningRequest signingRequestOffice = new SigningRequest();
        signingRequestOffice.setSigningRequestContents(lstTemp);
        signingRequestOffice.setTokenInfo(tokenInfo);
        signingRequestOffice.setOptional(optional);
        SigningResponse signingDataResponse = officeSigningService.sign(signingRequestOffice);
        return signingDataResponse;
    }
}
