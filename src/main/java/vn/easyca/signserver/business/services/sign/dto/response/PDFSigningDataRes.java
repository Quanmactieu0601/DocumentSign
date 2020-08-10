package vn.easyca.signserver.business.services.sign.dto.response;

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
