package vn.easyca.signserver.webapp.web.rest.mapper;

import vn.easyca.signserver.core.dto.signing.request.SigningRequest;
import vn.easyca.signserver.webapp.utils.DateTimeUtils;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.SigningVM;

import java.text.ParseException;
import java.util.Date;

public class SigningVMMapper<D,V> {


    private OptionalVMMapper optionalVMMapper = new OptionalVMMapper();

    private TokenVMMapper tokenVMMapper = new TokenVMMapper();

    private SigningDataVMMapper<D,V> dataVMMapper;

    public SigningVMMapper(SigningDataVMMapper<D, V> dataVMMapper) {
        this.dataVMMapper = dataVMMapper;
    }

    public SigningRequest<D> map(SigningVM<V> signingVM){
        SigningRequest<D> res= new SigningRequest<D>();
        res.setOptional(optionalVMMapper.map(signingVM.getOptional()));
        res.setTokenInfoDTO(tokenVMMapper.map(signingVM.getTokenInfo()));
        res.setSigner(signingVM.getSigner());
        try {
            res.setSignDate(DateTimeUtils.parse(signingVM.getSignDate()));
        } catch (ParseException e) {
            res.setSignDate(new Date());
        }
        res.setData(dataVMMapper.map(signingVM.getData()));
        return res;
    }

    public interface SigningDataVMMapper<D,V>{
        D map(V viewModel);
    }
}
