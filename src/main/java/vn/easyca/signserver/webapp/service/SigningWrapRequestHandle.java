package vn.easyca.signserver.webapp.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import vn.easyca.signserver.core.dto.OptionalDTO;
import vn.easyca.signserver.core.dto.sign.TokenInfoDTO;


public interface SigningWrapRequestHandle {
    ObjectMapper mapper = new ObjectMapper();
    Object sign(Object requestValue, TokenInfoDTO tokenInfo, OptionalDTO optional) throws Exception;
}
