package vn.easyca.signserver.webapp.service.dto;

import org.apache.commons.io.IOUtils;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.pki.sign.utils.FileUtils;
import vn.easyca.signserver.pki.sign.utils.StringUtils;
import vn.easyca.signserver.webapp.service.FileResourceService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SignatureExampleDTO {
    private String htmlTemplate;
    private String signer;
    private String signingImage;
    private Integer width;
    private Integer height;
    private boolean transparency;

    public String getHtmlTemplate() {
        return htmlTemplate;
    }

    public String getHtmlTemplate(FileResourceService fileResourceService) throws ApplicationException, IOException {
        return StringUtils.isNullOrEmpty(htmlTemplate) ? IOUtils.toString(fileResourceService.getTemplateFile("/templates/signature/signature.html"), StandardCharsets.UTF_8.name()) : htmlTemplate;
    }

    public void setHtmlTemplate(String htmlTemplate) {
        this.htmlTemplate = htmlTemplate;
    }

    public String getSigner() {
        return StringUtils.isNullOrEmpty(signer) ? "Lê Duy Thanh" : signer;
    }

    public void setSigner(String signer) {
        this.signer = signer;
    }

    public String getSigningImage() {
        return signingImage;
    }

    public String getSigningImage(FileResourceService fileResourceService) throws ApplicationException, IOException {
        return StringUtils.isNullOrEmpty(signingImage) ? Base64.getEncoder().encodeToString(IOUtils.toByteArray(fileResourceService.getTemplateFile("/templates/signature/SigningImageExam.jpg"))) : signingImage;
    }


    public void setSigningImage(String signingImage) {
        this.signingImage = signingImage;
    }

    public int getWidth() {
        return width == null ? 355 : width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height == null ? 130: height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isTransparency() {
        return transparency;
    }

    public void setTransparency(boolean transparency) {
        this.transparency = transparency;
    }
}
