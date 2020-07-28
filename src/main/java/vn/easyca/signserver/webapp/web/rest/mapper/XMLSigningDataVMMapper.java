package vn.easyca.signserver.webapp.web.rest.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import vn.easyca.signserver.webapp.service.dto.signing.request.XMLSigningData;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.XMLSigningDataVM;

@Component
public class XMLSigningDataVMMapper implements SigningVMMapper.SigningDataVMMapper<XMLSigningData, XMLSigningDataVM> {
    @Override
    public XMLSigningData map(XMLSigningDataVM viewModel) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(viewModel,XMLSigningData.class);
    }
}
