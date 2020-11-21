package vn.easyca.signserver.pki.sign.integrated.office;


import javafx.util.Pair;
import vn.easyca.signserver.pki.sign.rawsign.RawSigner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * Created by chen on 7/21/17.
 */
public class OfficeSigner extends AbstractOOXMLSignatureService {

    private OOXMLTestDataStorage temporaryDataStorage = new OOXMLTestDataStorage();
    private ByteArrayOutputStream signedOOXMLOutputStream = new ByteArrayOutputStream();
    private byte[] ooxmlData;

    public Pair signOOXMLFile(byte[] fileData, PrivateKey privateKey, List<X509Certificate> certificateChain, String hashAlgorithm)
            throws Exception {
        this.ooxmlData = fileData;
        DigestInfo digestInfo = preSign(null, null);

        RawSigner rawSigner = new RawSigner();
        byte[] signatureValue =  rawSigner.signData(digestInfo.digestValue, privateKey);
//        Signature sign = Signature.getInstance("SHA1withRSA");
//        sign.initSign(privateKey);
//        sign.update(digestInfo.digestValue);
//        byte[] signatureValue = sign.sign();
        byte[] signedDocument =  signOOXMLFile(signatureValue, certificateChain);
        return new Pair<>(signatureValue, signedDocument);
    }

    private byte[] signOOXMLFile(byte[] signatureValue, List<X509Certificate> certificateChain)
            throws Exception {
        postSign(signatureValue, certificateChain);
        return getSignedOfficeOpenXMLDocumentData();
    }

    protected OutputStream getSignedOfficeOpenXMLDocumentOutputStream() {
        return this.signedOOXMLOutputStream;
    }

    protected InputStream getOfficeOpenXMLDocumentInputStream() {
        byte[] buff = new byte[this.ooxmlData.length];
        System.arraycopy(this.ooxmlData, 0, buff, 0, this.ooxmlData.length);
        return new ByteArrayInputStream(buff);
    }

    protected TemporaryDataStorage getTemporaryDataStorage() {
        return this.temporaryDataStorage;
    }

    private byte[] getSignedOfficeOpenXMLDocumentData() {
        return this.signedOOXMLOutputStream.toByteArray();
    }

    static {
        OOXMLProvider.install();
    }
}
