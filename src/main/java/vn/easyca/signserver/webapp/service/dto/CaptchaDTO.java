package vn.easyca.signserver.webapp.service.dto;

public class CaptchaDTO {
    private String captchaText;
    private String captchaImg;

    public CaptchaDTO(String captchaText, String captchaImg) {
        this.captchaText = captchaText;
        this.captchaImg = captchaImg;
    }

    public String getCaptchaText() {
        return captchaText;
    }

    public void setCaptchaText(String captchaText) {
        this.captchaText = captchaText;
    }

    public String getCaptchaImg() {
        return captchaImg;
    }

    public void setCaptchaImg(String captchaImg) {
        this.captchaImg = captchaImg;
    }
}
