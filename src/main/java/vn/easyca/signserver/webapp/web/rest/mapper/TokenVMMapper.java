package vn.easyca.signserver.webapp.web.rest.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.webapp.service.dto.signing.TokenInfoDTO;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.TokenVM;

@Service
public class TokenVMMapper {

    public TokenInfoDTO map(TokenVM tokenVM){
        ModelMapper modelMapper=  new ModelMapper();
        return modelMapper.map(tokenVM,TokenInfoDTO.class);
    }

}
