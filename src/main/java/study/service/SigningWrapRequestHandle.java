package study.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import study.web.rest.vm.response.SigningResult;
import vn.easyca.signserver.core.dto.OptionalDTO;
import vn.easyca.signserver.core.dto.sign.TokenInfoDTO;

public interface SigningWrapRequestHandle {
    ObjectMapper mapper = new ObjectMapper();
    SigningResult sign(Object requestValue, TokenInfoDTO tokenInfo, OptionalDTO optional, String key) throws Exception;
}
