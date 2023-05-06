package core.dto.sign.newrequest;

public class ExtraInfo {

    private int pageNum = 1;
    private String reason = "";
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
