package vn.easyca.signserver.pki.sign.integrated.pdf.invisible;

import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.Calendar;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.exception.ApplicationException;

@Service
public class InvisiblePDFSigning {

    public byte[] signPdf(
        byte[] pdfToSign,
        String name,
        String reason,
        PrivateKey privateKey,
        Certificate[] certificates,
        String algorithm,
        String providerName
    ) throws ApplicationException {
        try {
            Signature signature = new Signature(privateKey, certificates, algorithm, providerName);
            //create temporary pdf file
            File pdfFile = File.createTempFile("pdf", "");
            //write bytes to created pdf file
            FileUtils.writeByteArrayToFile(pdfFile, pdfToSign);

            //create empty pdf file which will be signed
            File signedPdf = File.createTempFile("signedPdf", "");
            //sign pdfFile and write bytes to signedPdf
            this.signDetached(name, reason, signature, pdfFile, signedPdf);

            byte[] signedPdfBytes = Files.readAllBytes(signedPdf.toPath());

            //remove temporary files
            pdfFile.delete();
            signedPdf.delete();

            return signedPdfBytes;
        } catch (NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyStoreException | IOException e) {
            if (e instanceof NoSuchAlgorithmException) {
                throw new ApplicationException("Signing Algorithm not correct: " + algorithm, e);
            }
            if (e instanceof CertificateException) {
                throw new ApplicationException("Certificate has error", e);
            }
            if (e instanceof UnrecoverableKeyException) {
                throw new ApplicationException("Private key cannot recoverable", e);
            }
            if (e instanceof KeyStoreException) {
                throw new ApplicationException("Keystore has error", e);
            }
            if (e instanceof IOException) {
                throw new ApplicationException("IO Exception ", e);
            }
            return pdfToSign;
        }
    }

    private void signDetached(String name, String reason, SignatureInterface signature, File inFile, File outFile) throws IOException {
        if (inFile == null || !inFile.exists()) {
            throw new FileNotFoundException("Document for signing does not exist");
        }

        try (FileOutputStream fos = new FileOutputStream(outFile); PDDocument doc = PDDocument.load(inFile)) {
            signDetached(name, reason, signature, doc, fos);
        }
    }

    private void signDetached(String name, String reason, SignatureInterface signature, PDDocument document, OutputStream output)
        throws IOException {
        PDSignature pdSignature = new PDSignature();
        pdSignature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
        pdSignature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
        pdSignature.setName(name);
        pdSignature.setReason(reason);

        // the signing date, needed for valid signature
        pdSignature.setSignDate(Calendar.getInstance());

        // register signature dictionary and sign interface
        document.addSignature(pdSignature, signature);

        // write incremental (only for signing purpose)
        // use saveIncremental to add signature, using plain save method may break up a document
        document.saveIncremental(output);
    }
}
