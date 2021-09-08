package vn.easyca.signserver.webapp.service.impl.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.easyca.signserver.core.dto.OptionalDTO;
import vn.easyca.signserver.core.dto.sign.TokenInfoDTO;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningRequest;
import vn.easyca.signserver.core.dto.sign.newrequest.SigningRequestContent;
import vn.easyca.signserver.core.dto.sign.newresponse.SigningResponse;
import vn.easyca.signserver.core.services.XMLSigningService;
import vn.easyca.signserver.webapp.service.SigningWrapRequestHandle;

import java.util.ArrayList;
import java.util.List;
@Service
@Transactional
public class XmlSigningRequest implements SigningWrapRequestHandle {

    private final XMLSigningService xmlSigningService;
    public XmlSigningRequest(XMLSigningService xmlSigningService) {
        this.xmlSigningService = xmlSigningService;
    }

    @Override
    public Object sign(Object requestValue, TokenInfoDTO tokenInfo, OptionalDTO optional) throws Exception {
        SigningRequestContent signingRequestContent = mapper.convertValue(requestValue, SigningRequestContent.class);
        List<SigningRequestContent> lstTemp = new ArrayList<>();
        lstTemp.add(signingRequestContent);
        SigningRequest signingRequestXml = new SigningRequest();
        signingRequestXml.setSigningRequestContents(lstTemp);
        signingRequestXml.setOptional(optional);
        signingRequestXml.setTokenInfo(tokenInfo);
        SigningResponse signingDataResponse = xmlSigningService.sign(signingRequestXml);
        return signingDataResponse;
    }
}
