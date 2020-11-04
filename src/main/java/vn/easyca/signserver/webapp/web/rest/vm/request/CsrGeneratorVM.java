package vn.easyca.signserver.webapp.web.rest.vm.request;

public class CsrGeneratorVM {
    private Long userId;
    private int keyLen;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getKeyLen() {
        return keyLen;
    }

    public void setKeyLen(int keyLen) {
        this.keyLen = keyLen;
    }
}
