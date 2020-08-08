package vn.easyca.signserver.webapp.web.rest.mapper;

import org.modelmapper.ModelMapper;
import vn.easyca.signserver.business.services.dto.OptionalDTO;
import vn.easyca.signserver.business.services.signing.dto.TokenInfoDTO;
import vn.easyca.signserver.business.services.signing.dto.request.SignRequest;
import vn.easyca.signserver.webapp.utils.DateTimeUtils;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.SignElementVM;
import vn.easyca.signserver.webapp.web.rest.vm.request.sign.SigningVM;
import java.util.Date;
import java.util.Map;


public class SignVMMapper<D, V> {


    public SignRequest<D> map(SigningVM<V> signVM, Class<D> contentClass) {
        ModelMapper mapper = new ModelMapper();
        SignRequest<D> res = new SignRequest<D>();
        if (signVM.getOptional() != null)
            res.setOptional(mapper.map(signVM.getOptional(), OptionalDTO.class));
        if (signVM.getTokenInfo() != null)
            res.setTokenInfoDTO(mapper.map(signVM.getTokenInfo(), TokenInfoDTO.class));
        Map<String, SignElementVM<V>> elements = signVM.getElements();
        for (Map.Entry<String, SignElementVM<V>> entry : elements.entrySet()) {
            Date signDate = new Date();
            if (entry.getValue().getSignDate() != null)
                signDate = DateTimeUtils.tryParse(entry.getValue().getSignDate(),new Date());
            String signer = entry.getValue().getSigner();
            D content = mapper.map(entry.getValue().getContent(),contentClass);
            res.Add(entry.getKey(),content,signDate,signer);
        }
        return res;
    }
}
