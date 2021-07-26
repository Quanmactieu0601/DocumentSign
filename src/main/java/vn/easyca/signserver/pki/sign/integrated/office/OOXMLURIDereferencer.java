package vn.easyca.signserver.pki.sign.integrated.office;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;

import javax.xml.crypto.*;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class OOXMLURIDereferencer
        implements URIDereferencer {

    private final InputStream ooxmlIn;
    private final URIDereferencer baseUriDereferencer;

    public OOXMLURIDereferencer(InputStream ooxmlIn) {
        this.ooxmlIn = ooxmlIn;
        XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance();

        this.baseUriDereferencer = xmlSignatureFactory.getURIDereferencer();
    }

    public Data dereference(URIReference uriReference, XMLCryptoContext context) throws URIReferenceException {
        if (null == uriReference) {
            throw new NullPointerException("URIReference cannot be null");
        }
        if (null == context) {
            throw new NullPointerException("XMLCrytoContext cannot be null");
        }

        String uri = uriReference.getURI();
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try (InputStream dataInputStream = findDataInputStream(uri)) {
            this.ooxmlIn.reset();
            if (null == dataInputStream) {
                return this.baseUriDereferencer.dereference(uriReference, context);
            }
            return new OctetStreamData(dataInputStream, uri, null);
        } catch (IOException e) {
            throw new URIReferenceException("I/O error: " + e.getMessage(), e);
        } catch (InvalidFormatException e) {
            throw new URIReferenceException("Invalid format error: " + e.getMessage(), e);
        }
    }

    private InputStream findDataInputStream(String uri)
            throws IOException, InvalidFormatException {
        if (-1 != uri.indexOf("?")) {
            uri = uri.substring(0, uri.indexOf("?"));
        }
        OPCPackage pkg = OPCPackage.open(this.ooxmlIn);
        for (PackagePart part : pkg.getParts()) {
            if (uri.equals(part.getPartName().getURI().toString())) {
                return part.getInputStream();
            }
        }
        return null;
    }
}
