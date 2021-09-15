package vn.easyca.signserver.webapp.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import vn.easyca.signserver.core.dto.OptionalDTO;
import vn.easyca.signserver.core.dto.sign.TokenInfoDTO;
import vn.easyca.signserver.webapp.web.rest.vm.response.SigningResult;


public interface SigningWrapRequestHandle {
    ObjectMapper mapper = new ObjectMapper();
    SigningResult sign(Object requestValue, TokenInfoDTO tokenInfo, OptionalDTO optional, String key) throws Exception;
}
