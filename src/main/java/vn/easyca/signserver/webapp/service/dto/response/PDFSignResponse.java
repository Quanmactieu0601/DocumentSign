package vn.easyca.signserver.webapp.service.dto.response;

public class PDFSignResponse {

    private byte[] content;

    private int status;

    private String errMsg;

    public PDFSignResponse(byte[] content) {
        this.content = content;
    }

    public PDFSignResponse(int status, String errMsg) {
        this.status = status;
        this.errMsg = errMsg;
    }

    public byte[] getContent() {
        return content;
    }

    public int getStatus() {
        return status;
    }

    public String getErrMsg() {
        return errMsg;
    }
}
