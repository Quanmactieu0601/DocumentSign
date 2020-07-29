package vn.easyca.signserver.sign.core.sign.integrated.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SignXMLLib {

    private static final String CONTENT_ID = "SigningData";

    public String generateXMLDigitalSignature(SignXMLDto signXMLDto) throws MarshalException,
        XPathExpressionException,
        InvalidAlgorithmParameterException,
        NoSuchAlgorithmException,
        XMLSignatureException,
        TransformerException, IOException, SAXException, ParserConfigurationException {

        XMLSignatureFactory xmlSigFactory = XMLSignatureFactory.getInstance("DOM");
        Document doc = parseDoc(signXMLDto.getXml());
        Node content = getContentSign(doc);
        DOMSignContext domSignCtx = new DOMSignContext(signXMLDto.getPrivateKey(), doc.getDocumentElement());
        domSignCtx.setIdAttributeNS((Element) content, null, "Id");
        Reference ref = null;
        SignedInfo signedInfo = null;
        ref = xmlSigFactory.newReference("#" + CONTENT_ID,
            xmlSigFactory.newDigestMethod(DigestMethod.SHA1, null),
            Collections.singletonList(xmlSigFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
            null,
            null);
        signedInfo = xmlSigFactory.newSignedInfo(
            xmlSigFactory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE,
                (C14NMethodParameterSpec) null),
            xmlSigFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
            Collections.singletonList(ref));
        KeyInfo keyInfo = getKeyInfo(xmlSigFactory, signXMLDto.getX509Certificate());
        XMLSignature xmlSignature = xmlSigFactory.newXMLSignature(signedInfo, keyInfo);
        xmlSignature.sign(domSignCtx);
        return docToString(doc);
    }

    private String docToString(Document doc) throws TransformerException {


        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        StringWriter sw = new StringWriter();
        t.transform(new DOMSource(doc), new StreamResult(sw));
        return sw.toString();
    }

    private Node getContentSign(Document doc) throws XPathExpressionException {

        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        return (Node) xpath.evaluate(String.format("//*[@Id=\"%s\"] | //*[@id=\"%s\"]", CONTENT_ID, CONTENT_ID), doc, XPathConstants.NODE);
    }

    private KeyInfo getKeyInfo(XMLSignatureFactory xmlSigFactory, X509Certificate certificate) {

        KeyInfoFactory kif = xmlSigFactory.getKeyInfoFactory();
        List x509Content = new ArrayList();
        x509Content.add(certificate.getSubjectX500Principal().getName());
        x509Content.add(certificate);
        X509Data xd = kif.newX509Data(x509Content);
        KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));
        return ki;
    }

    private Document parseDoc(String xml) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xml)));
        return doc;
    }
}
