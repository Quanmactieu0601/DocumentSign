package vn.easyca.signserver.webapp.web.rest.vm.request;

public class SignXmlRequestVM extends BaseSignRequestVM{


    private  String xml;

    private  String contentTag;

    public String getXml() {
        return xml;
    }

    public String getContentTag() {
        return contentTag;
    }

    public void setContentTag(String contentTag) {
        this.contentTag = contentTag;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }
}
