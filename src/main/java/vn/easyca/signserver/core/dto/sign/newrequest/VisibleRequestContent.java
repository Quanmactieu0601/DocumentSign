package vn.easyca.signserver.core.dto.sign.newrequest;

public class VisibleRequestContent extends SigningRequestContent {
    private Location location;
    private ExtraInfo extraInfo;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ExtraInfo getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(ExtraInfo extraInfo) {
        this.extraInfo = extraInfo;
    }
}

class Location {
    private int visibleX = 0;
    private int visibleY = 0;
    private int visibleWidth = 150;
    private int visibleHeight = 50;

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

class ExtraInfo {
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
