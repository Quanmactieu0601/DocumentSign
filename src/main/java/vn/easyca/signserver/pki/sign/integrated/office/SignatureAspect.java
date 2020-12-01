package vn.easyca.signserver.pki.sign.integrated.office;

import org.w3c.dom.Document;

import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface SignatureAspect {

    void preSign(XMLSignatureFactory paramXMLSignatureFactory, Document paramDocument, String paramString, List<Reference> paramList, List<XMLObject> paramList1)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;
}
