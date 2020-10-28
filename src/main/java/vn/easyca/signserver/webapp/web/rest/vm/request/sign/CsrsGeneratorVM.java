package vn.easyca.signserver.webapp.web.rest.vm.request.sign;

import java.util.List;

public class CsrsGeneratorVM {
    private List<Long> userIds;
    private int keyLen;

    public int getKeyLen() {
        return keyLen;
    }

    public void setKeyLen(int keyLen) {
        this.keyLen = keyLen;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }
}
