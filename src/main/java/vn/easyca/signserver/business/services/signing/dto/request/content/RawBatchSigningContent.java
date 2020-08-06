package vn.easyca.signserver.business.services.signing.dto.request.content;

import java.util.HashMap;

public class RawBatchSigningContent {
    private HashMap<String,String> batch;

    public HashMap<String, String> getBatch() {
        return batch;
    }

    public void setBatch(HashMap<String, String> batch) {
        this.batch = batch;
    }
}
