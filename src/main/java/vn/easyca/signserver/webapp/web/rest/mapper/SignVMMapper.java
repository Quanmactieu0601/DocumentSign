package vn.easyca.signserver.webapp.web.rest.mapper;

import org.modelmapper.ModelMapper;
import vn.easyca.signserver.core.dto.OptionalDTO;
import vn.easyca.signserver.core.dto.sign.TokenInfoDTO;
import vn.easyca.signserver.core.dto.sign.request.SignRequest;
import vn.easyca.signserver.webapp.utils.DateTimeUtils;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.SignElementVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.SigningVM;
import java.util.Date;

public class SignVMMapper<D, V> {
    public SignRequest<D> map(SigningVM<V> signVM, Class<D> contentClass) {
        ModelMapper mapper = new ModelMapper();
        SignRequest<D> res = new SignRequest<D>();
        if (signVM.getOptional() != null)
            res.setOptional(mapper.map(signVM.getOptional(), OptionalDTO.class));
        if (signVM.getTokenInfo() != null)
            res.setTokenInfoDTO(mapper.map(signVM.getTokenInfo(), TokenInfoDTO.class));
        for (SignElementVM<V> signElementVM : signVM.getElements()) {
            Date signDate = new Date();
            if (signElementVM.getSignDate() != null)
                signDate = DateTimeUtils.tryParse(signElementVM.getSignDate(),new Date());
            String signer = signElementVM.getSigner();
            D content = mapper.map(signElementVM.getContent(),contentClass);
            res.Add(signElementVM.getKey(),content,signDate,signer);
        }
        return res;
    }

}
