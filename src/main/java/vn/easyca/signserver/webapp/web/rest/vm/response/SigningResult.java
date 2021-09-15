package vn.easyca.signserver.webapp.web.rest.vm.response;

public class SigningResult {
    private String signedFile;
    private String key;
    private String msg;
    private int status;

    public SigningResult(String signedFile, String key, String msg, int status) {
        this.signedFile = signedFile;
        this.key = key;
        this.msg = msg;
        this.status = status;
    }

    public String getSignedFile() { return signedFile; }

    public void setSignedFile(String signedFile) { this.signedFile = signedFile; }

    public String getKey() { return key; }

    public void setKey(String key) { this.key = key; }

    public String getMsg() { return msg; }

    public void setMsg(String msg) { this.msg = msg; }

    public int getStatus() { return status; }

    public void setStatus(int status) { this.status = status; }

}
