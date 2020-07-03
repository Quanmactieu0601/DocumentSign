package vn.easyca.signserver.webapp.web.rest.vm.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignResponseVM {

    private int status;

    private Object data;

    public SignResponseVM(int status, Object data) {
        this.status = status;
        this.data = data;
    }


}
