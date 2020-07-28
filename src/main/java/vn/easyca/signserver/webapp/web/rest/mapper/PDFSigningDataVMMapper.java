package vn.easyca.signserver.webapp.web.rest.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import vn.easyca.signserver.webapp.service.dto.signing.request.PDFSigningData;
import vn.easyca.signserver.webapp.web.rest.vm.sign.PDFSigningDataVM;

@Component
public class PDFSigningDataVMMapper implements SigningVMMapper.SigningDataVMMapper<PDFSigningData, PDFSigningDataVM> {

    @Override
    public PDFSigningData map(PDFSigningDataVM viewModel) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(viewModel,PDFSigningData.class);
    }
}
