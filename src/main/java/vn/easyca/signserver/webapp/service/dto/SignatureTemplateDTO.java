package vn.easyca.signserver.webapp.service.dto;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.apache.xpath.operations.Bool;
import vn.easyca.signserver.webapp.domain.SignatureTemplate;
import vn.easyca.signserver.webapp.enm.SignatureTemplateParserType;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link vn.easyca.signserver.webapp.domain.SignatureTemplate} entity.
 */
public class SignatureTemplateDTO implements Serializable {


    private Long id;

    private Long userId;

    private String createdBy;

    private LocalDateTime createdDate;

    private String coreParser;

    private String fullName;

    private Integer width;

    private Integer height;

    private String htmlTemplate;

    private Boolean transparency;

    private String thumbnail;

    private Boolean activated;


    public SignatureTemplateDTO(Long id, Long userId, String createdBy, LocalDateTime createdDate, String coreParser, String fullName, Integer width, Integer height, String htmlTemplate, Boolean transparency, String thumbnail, Boolean activated) {
        this.id = id;
        this.userId = userId;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.coreParser = coreParser;
        this.fullName = fullName;
        this.width = width;
        this.height = height;
        this.htmlTemplate = htmlTemplate;
        this.transparency = transparency;
        this.thumbnail = thumbnail;
        this.activated = activated;
    }

    public SignatureTemplateDTO(Long id, Long userId, String createdBy, LocalDateTime createdDate,
                                String htmlTemplate, SignatureTemplateParserType coreParser,
                                Integer width, Integer height, String thumbnail, Boolean activated, String fullName ) {
        this.id = id;
        this.userId = userId;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.coreParser = coreParser.toString();
        this.thumbnail = thumbnail;
        this.fullName = fullName;
        this.width = width;
        this.height = height;
        this.htmlTemplate = htmlTemplate;
        this.activated = activated;
    }

    public SignatureTemplateDTO(Long id, Long userId, String createdBy, LocalDateTime createdDate,
                                String htmlTemplate, SignatureTemplateParserType coreParser,
                                Integer width, Integer height, String fullName, Boolean transparency, Boolean activated) {
        this.id = id;
        this.userId = userId;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.coreParser = coreParser.toString();
        this.fullName = fullName;
        this.width = width;
        this.height = height;
        this.htmlTemplate = htmlTemplate;
        this.transparency = transparency;
        this.activated = activated;
    }

    public SignatureTemplateDTO() {

    }

    public SignatureTemplateDTO(SignatureTemplate signatureTemplate) {
        this.id = signatureTemplate.getId();
        this.userId = signatureTemplate.getUserId();
        this.createdBy = signatureTemplate.getCreatedBy();
        this.createdDate = signatureTemplate.getCreatedDate();
        this.coreParser = signatureTemplate.getCoreParser().toString();
        this.width = signatureTemplate.getWidth();
        this.height = signatureTemplate.getHeight();
        this.htmlTemplate = signatureTemplate.getHtmlTemplate();
        this.transparency = signatureTemplate.getTransparency();
        this.thumbnail = signatureTemplate.getThumbnail();
        this.activated = signatureTemplate.getActivated();
    }

    public String getFullName() { return fullName; }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCoreParser() { return coreParser; }

    public void setCoreParser(String coreParser) { this.coreParser = coreParser; }

    public String getHtmlTemplate() { return htmlTemplate; }

    public void setHtmlTemplate(String htmlTemplate) { this.htmlTemplate = htmlTemplate; }

    public String getCreatedBy() { return createdBy; }

    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedDate() { return createdDate; }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public Integer getWidth() { return width; }

    public void setWidth(Integer width) { this.width = width; }

    public Integer getHeight() { return height; }

    public void setHeight(Integer height) { this.height = height; }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getTransparency() {
        return transparency;
    }

    public void setTransparency(Boolean transparency) {
        this.transparency = transparency;
    }

    public String getThumbnail() { return thumbnail; }

    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SignatureTemplateDTO)) {
            return false;
        }

        return id != null && id.equals(((SignatureTemplateDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SignatureTemplateDTO{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            "}";
    }


}
