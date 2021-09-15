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
import vn.easyca.signserver.webapp.web.rest.vm.response.SigningResult;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
@Service
@Transactional
public class XmlSigningRequest implements SigningWrapRequestHandle {

    private final XMLSigningService xmlSigningService;
    public XmlSigningRequest(XMLSigningService xmlSigningService) {
        this.xmlSigningService = xmlSigningService;
    }
    private final int RESULT_OK = 0;
    private final int RESULT_ERROR = 1;
    @Override
    public SigningResult sign(Object requestValue, TokenInfoDTO tokenInfo, OptionalDTO optional, String key) throws Exception {
        SigningRequestContent signingRequestContent = mapper.convertValue(requestValue, SigningRequestContent.class);
        List<SigningRequestContent> lstTemp = new ArrayList<>();
        lstTemp.add(signingRequestContent);
        SigningRequest signingRequestXml = new SigningRequest();
        signingRequestXml.setSigningRequestContents(lstTemp);
        signingRequestXml.setOptional(optional);
        signingRequestXml.setTokenInfo(tokenInfo);
        SigningResponse signingDataResponse = xmlSigningService.sign(signingRequestXml);

        byte[] signedContent = signingDataResponse.getResponseContentList().get(0).getSignedDocument();
        String base64Encoded = Base64.getEncoder().encodeToString(signedContent);
        return new SigningResult(base64Encoded, key, "Ký tệp thành công", RESULT_OK);
    }
}
