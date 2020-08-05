package vn.easyca.signserver.webapp.web.rest.mapper;

import org.modelmapper.ModelMapper;
import vn.easyca.signserver.business.services.dto.OptionalDTO;
import vn.easyca.signserver.business.services.signing.dto.TokenInfoDTO;
import vn.easyca.signserver.business.services.signing.dto.request.SigningRequest;
import vn.easyca.signserver.webapp.utils.DateTimeUtils;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.OptionalVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.SigningVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.TokenVM;

import java.text.ParseException;
import java.util.Date;

class OptionalVMMapper {

    public OptionalDTO map(OptionalVM optionalVM) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(optionalVM, OptionalDTO.class);
    }
}

class TokenVMMapper {

    public TokenInfoDTO map(TokenVM tokenVM) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(tokenVM, TokenInfoDTO.class);
    }

}

public class SigningVMMapper<D, V> {

    private final OptionalVMMapper optionalVMMapper = new OptionalVMMapper();

    private final TokenVMMapper tokenVMMapper = new TokenVMMapper();

    public SigningRequest<D> map(SigningVM<V> signingVM, Class<D> contentClass) {

        SigningRequest<D> res = new SigningRequest<D>();
        if (signingVM.getOptional() != null)
            res.setOptional(optionalVMMapper.map(signingVM.getOptional()));
        if (signingVM.getTokenInfo() != null)
            res.setTokenInfoDTO(tokenVMMapper.map(signingVM.getTokenInfo()));
        res.setSigner(signingVM.getSigner());
        try {
            if (signingVM.getSignDate() == null || signingVM.getSignDate().isEmpty())
                res.setSignDate(new Date());
            else
                res.setSignDate(DateTimeUtils.parse(signingVM.getSignDate()));
        } catch (ParseException e) {
            res.setSignDate(new Date());
        }
        if(signingVM.getContent() != null) {
            ModelMapper contentMapper = new ModelMapper();
            D content =  contentMapper.map(signingVM.getContent(), contentClass);
            res.setContent(content);
        }
        return res;
    }
}
