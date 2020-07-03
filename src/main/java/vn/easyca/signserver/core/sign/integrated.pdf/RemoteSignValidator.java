//package vn.psvm.signserver.integrated.pdf;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.security.PdfPKCS7;
//import vn.psvm.signserver.pdfsigner.PDFValidationResult;

import java.io.InputStream;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by chen on 7/26/17.
 */
public class RemoteSignValidator {
//    private PDFValidationResult verify(PdfReader reader) throws Exception {
////        KeyStore kall = PdfPKCS7.loadCacertsKeyStore();
//        AcroFields af = reader.getAcroFields();
//
//        PDFValidationResult resultObj = new PDFValidationResult();
//        // Search of the whole signature
//        ArrayList<String> names = af.getSignatureNames();
//
//        // For each signature
//        for (String name : names) {
//            PdfPKCS7 pk = af.verifySignature(name);
//            Calendar cal = pk.getSignDate();
//            Certificate pkc[] = pk.getCertificates();
//            // document is modified or not?
//            if (!pk.verify()) {
//                resultObj.setStatus(0);
//                resultObj.setMessage("Document is modified");
//                return resultObj;
//            }
//        }
//        resultObj.setStatus(1);
//        resultObj.setMessage("OK");
//        return resultObj;
//    }
//
//    public PDFValidationResult verify(byte[] data) throws Exception {
//        PdfReader reader = new PdfReader(data);
//        return verify(reader);
//    }
//
//    public PDFValidationResult verify(InputStream is) throws Exception {
//        PdfReader reader = new PdfReader(is);
//        return verify(reader);
//    }
//
//    public PDFValidationResult verify(String fileName) throws Exception {
//        PdfReader reader = new PdfReader(fileName);
//        return verify(reader);
//    }
}
