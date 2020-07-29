package vn.easyca.signserver.webapp.web.rest.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import vn.easyca.signserver.core.dto.signing.request.RawSigningData;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.RawSigningDataVM;

@Component
public class RawSigningDataVMMapper implements SigningVMMapper.SigningDataVMMapper<RawSigningData, RawSigningDataVM> {


    @Override
    public RawSigningData map(RawSigningDataVM viewModel) {
        ModelMapper modelMapper= new ModelMapper();
        return modelMapper.map(viewModel,RawSigningData.class);
    }
}
