package vn.easyca.signserver.webapp.service.dto.request;

public class SignXMLRequest {

    private final String xml;

    private final String contentTag;

    public SignXMLRequest(String xml, String contentId) {
        this.xml = xml;
        this.contentTag = contentId;
    }

    public String getContentTag() {
        return contentTag;
    }

    public String getXml() {
        return xml;
    }
}
