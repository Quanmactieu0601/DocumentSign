package vn.easyca.signserver.webapp.web.rest.mapper;

import org.modelmapper.ModelMapper;
import vn.easyca.signserver.webapp.service.signer.OptionalDTO;
import vn.easyca.signserver.webapp.web.rest.vm.sign.OptionalVM;
public class OptionalVMMapper {

    public OptionalDTO map(OptionalVM optionalVM){
        ModelMapper modelMapper= new ModelMapper();
        return modelMapper.map(optionalVM,OptionalDTO.class);
    }
}
