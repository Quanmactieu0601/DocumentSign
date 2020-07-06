package vn.easyca.signserver.webapp.service.dto.request;

import vn.easyca.signserver.webapp.service.dto.SignRequestDto;
import vn.easyca.signserver.webapp.service.dto.TokenInfoDto;
public class SignPDFRequest extends SignRequestDto {

    private byte[] content;

    private SignPDFVisible visible = new SignPDFVisible();

    private SignPDFSignatureInfo info = new SignPDFSignatureInfo();

    public SignPDFRequest(TokenInfoDto tokenInfoDto,String signer, byte[] content) {
        super(tokenInfoDto,signer);
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public SignPDFVisible getVisible() {
        return visible;
    }

    public void setVisible(SignPDFVisible visible) {
        this.visible = visible;
    }

    public SignPDFSignatureInfo getInfo() {
        return info;
    }

    public void setInfo(SignPDFSignatureInfo info) {
        this.info = info;
    }


    public static class SignPDFVisible {

        private int visibleX = 0;

        private int visibleY = 0;

        private int visibleWidth = 150;

        private int visibleHeight = 100;

        public int getVisibleX() {
            return visibleX;
        }

        public void setVisibleX(int visibleX) {
            this.visibleX = visibleX;
        }

        public int getVisibleY() {
            return visibleY;
        }

        public void setVisibleY(int visibleY) {
            this.visibleY = visibleY;
        }

        public int getVisibleWidth() {
            return visibleWidth;
        }

        public void setVisibleWidth(int visibleWidth) {
            this.visibleWidth = visibleWidth;
        }

        public int getVisibleHeight() {
            return visibleHeight;
        }

        public void setVisibleHeight(int visibleHeight) {
            this.visibleHeight = visibleHeight;
        }
    }

    public static class SignPDFSignatureInfo {

        private int pageNum = 1;

        private String reason = "";

        private String location = "";

        private String signerLabel = "Người ký";

        private String signDateLabel = "Ngày ký";

        public int getPageNum() {
            return pageNum;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getSignerLabel() {
            return signerLabel;
        }

        public void setSignerLabel(String signerLabel) {
            this.signerLabel = signerLabel;
        }

        public String getSignDateLabel() {
            return signDateLabel;
        }

        public void setSignDateLabel(String signDateLabel) {
            this.signDateLabel = signDateLabel;
        }
    }


}
