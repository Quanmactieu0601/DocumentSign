package vn.easyca.signserver.webapp.web.rest.vm.request.sign;

import java.util.HashMap;

public class RawBatchSigningVM {

    private HashMap<String,String> batch;

    public HashMap<String, String> getBatch() {
        return batch;
    }

    public void setBatch(HashMap<String, String> batch) {
        this.batch = batch;
    }
}
