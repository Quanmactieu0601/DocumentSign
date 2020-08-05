package vn.easyca.signserver.pki.sign.integrated.xml;


import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

public class SignXMLDto {

    private String xml;

    private PrivateKey privateKey;

    private PublicKey publicKey;

    private X509Certificate x509Certificate;


    public SignXMLDto(String xml, PrivateKey privateKey, PublicKey publicKey, X509Certificate certificate) {
        this.xml = xml;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.x509Certificate = certificate;
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

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public X509Certificate getX509Certificate() {
        return x509Certificate;
    }
}
