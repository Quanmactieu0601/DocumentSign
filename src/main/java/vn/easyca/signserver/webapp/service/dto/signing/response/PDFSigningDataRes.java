package vn.easyca.signserver.webapp.service.dto.signing.response;

public class PDFSigningDataRes {

    public PDFSigningDataRes(byte[] content) {
        this.content = content;
    }

    private byte[] content;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
