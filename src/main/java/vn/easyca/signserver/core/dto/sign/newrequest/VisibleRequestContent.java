package vn.easyca.signserver.core.dto.sign.newrequest;

public class VisibleRequestContent extends SigningRequestContent {
    private Location location;
    private ExtraInfo extraInfo;
    private String imageSignature;
    private Integer templateId;

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

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

    public String getImageSignature() { return imageSignature; }

    public void setImageSignature(String imageSignature) { this.imageSignature = imageSignature; }
}

