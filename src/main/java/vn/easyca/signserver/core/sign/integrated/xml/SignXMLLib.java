package vn.easyca.signserver.core.sign.integrated.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;


import javax.print.Doc;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Collections;

public class SignXMLLib {


    public String generateXMLDigitalSignature(SignXMLDto signXMLDto) throws MarshalException {
        //Create XML Signature Factory
        XMLSignatureFactory xmlSigFactory = XMLSignatureFactory.getInstance("DOM");
        Document doc = parseDoc(signXMLDto.getXml());
        Node content = getContentSign(doc,signXMLDto.getNameTagContent());
        DOMSignContext domSignCtx = new DOMSignContext(signXMLDto.getPrivateKey(), content);
        Reference ref = null;
        SignedInfo signedInfo = null;
        try {
            ref = xmlSigFactory.newReference("", xmlSigFactory.newDigestMethod(DigestMethod.SHA1, null),
                Collections.singletonList(xmlSigFactory.newTransform(Transform.ENVELOPED,
                    (TransformParameterSpec) null)), null, null);
            signedInfo = xmlSigFactory.newSignedInfo(
                xmlSigFactory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE,
                    (C14NMethodParameterSpec) null),
                xmlSigFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
                Collections.singletonList(ref));
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException ex) {
            ex.printStackTrace();
        }
        KeyInfo keyInfo = getKeyInfo(xmlSigFactory, signXMLDto.getPublicKey());
        XMLSignature xmlSignature = xmlSigFactory.newXMLSignature(signedInfo, keyInfo);
        try {
            //Sign the document
            xmlSignature.sign(domSignCtx);
        } catch (XMLSignatureException ex) {
            ex.printStackTrace();
        } catch (MarshalException e) {
            e.printStackTrace();
        }

        try {
            return docToString(doc);
        } catch (TransformerException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String docToString(Document doc) throws TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        StringWriter sw = new StringWriter();
        t.transform(new DOMSource(doc), new StreamResult(sw));
        String result = sw.toString();
        return result;
    }

    private Node getContentSign(Document doc, String tagName) {

        return  doc.getElementsByTagName(tagName).item(0);
    }

    private KeyInfo getKeyInfo(XMLSignatureFactory xmlSigFactory, PublicKey publicKey) {
        KeyInfo keyInfo = null;
        KeyValue keyValue = null;
        KeyInfoFactory keyInfoFact = xmlSigFactory.getKeyInfoFactory();
        try {
            keyValue = keyInfoFact.newKeyValue(publicKey);
        } catch (KeyException ex) {
            ex.printStackTrace();
        }
        keyInfo = keyInfoFact.newKeyInfo(Collections.singletonList(keyValue));
        return keyInfo;
    }


    private Document parseDoc(String xml) {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
