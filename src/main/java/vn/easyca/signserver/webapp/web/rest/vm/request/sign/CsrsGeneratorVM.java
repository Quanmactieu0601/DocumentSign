package vn.easyca.signserver.webapp.web.rest.vm.request.sign;

import java.util.List;

public class CsrsGeneratorVM {
    private List<Long> userIds;
    private int keyLen;

    public int getKeyLen() {
        return keyLen == 0 ? 1024 : keyLen;
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

    public CsrsGeneratorVM(List<Long> userIds, int keyLen) {
        this.userIds = userIds;
        this.keyLen = keyLen;
    }

    public CsrsGeneratorVM() {
    }
}
