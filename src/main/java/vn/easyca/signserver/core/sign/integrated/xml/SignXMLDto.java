package vn.easyca.signserver.core.sign.integrated.xml;


import java.security.PrivateKey;
import java.security.PublicKey;

public class SignXMLDto {

    private String xml;

    private String nameTagContent;

    private PrivateKey privateKey;

    private PublicKey publicKey;

    public SignXMLDto(String xml, String nameTagContent, PrivateKey privateKey, PublicKey publicKey) {
        this.xml = xml;
        this.nameTagContent = nameTagContent;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public String getNameTagContent() {
        return nameTagContent;
    }

    public void setNameTagContent(String nameTagContent) {
        this.nameTagContent = nameTagContent;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
