package vn.easyca.signserver.core.sign.integrated.pdf;


import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.io.RASInputStream;
import com.itextpdf.text.io.RandomAccessSource;
import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.io.StreamUtil;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ByteBuffer;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.ExternalBlankSignatureContainer;
import com.itextpdf.text.pdf.security.ExternalSignatureContainer;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;
import com.itextpdf.text.pdf.security.PdfPKCS7;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.Principal;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.easyca.signserver.core.sign.utils.DatetimeUtils;

public class SignPDFLib {
    private final Logger LOGGER = LoggerFactory.getLogger(SignPDFLib.class);
    private String hashAlg = "SHA1";
    private final String CRYPT_ALG = "RSA";
    private final int ESTIMATED_EXTERNAL_SIGNATURE_SIZE = 8192;

    public void setHashAlg(String hashAlg) {
        this.hashAlg = hashAlg;
    }

    public List<byte[]> createHash(byte[] content, String tempFile, Certificate[] chain, JSONObject signature_info, JSONObject visible_signature, Date signDate, String signField)
            throws Exception {
        String reason = "";
        if (signature_info.has("reason")) {
            reason = signature_info.getString("reason");
        }
        String location = "";
        if (signature_info.has("location")) {
            location = signature_info.getString("location");
        }
        String signerLabel = "Ký bởi";
        if (signature_info.has("signerLabel")) {
            signerLabel = signature_info.getString("signerLabel");
        }
        String signDateLabel = "Ký ngày";
        if (signature_info.has("signDateLabel")) {
            signDateLabel = signature_info.getString("signDateLabel");
        }
        int pageNum = 11;
        if (visible_signature.has("pageNum")) {
            pageNum = ((Integer) visible_signature.get("pageNum")).intValue();
        }
        int visibleX = 0;
        if (visible_signature.has("visibleX")) {
            visibleX = ((Integer) visible_signature.get("visibleX")).intValue();
        }
        int visibleY = 0;
        if (visible_signature.has("visibleY")) {
            visibleY = ((Integer) visible_signature.get("visibleY")).intValue();
        }
        int visibleWidth = 150;
        if (visible_signature.has("visibleWidth")) {
            visibleWidth = ((Integer) visible_signature.get("visibleWidth")).intValue();
        }
        int visibleHeight = 50;
        if (visible_signature.has("visibleHeight")) {
            visibleHeight = ((Integer) visible_signature.get("visibleHeight")).intValue();
        }
        if ((pageNum < 1) || (visibleX < 0) || (visibleY < 0) || (visibleWidth < 0) || (visibleHeight < 0)) {
            throw new Exception("Dữ liệu định dạng khung ký sai.");
        }
        emptySignature(content, tempFile, signField, (X509Certificate) chain[0], reason, location, signerLabel, signDateLabel,
                pageNum, visibleWidth, visibleHeight, visibleX, visibleY, signDate);

        return preSign(tempFile, signField, chain, signDate);
    }

    public void insertSignature(String src, String dest, byte[] hash, byte[] extSignature, Certificate[] chain, Date signDate, String signField)
            throws Exception {
        BouncyCastleProvider providerBC = new BouncyCastleProvider();
        Security.addProvider(providerBC);
        PdfReader reader = new PdfReader(src);
        FileOutputStream os = new FileOutputStream(dest);
        AcroFields af = reader.getAcroFields();
        PdfDictionary v = af.getSignatureDictionary(signField);
        if (v == null) {
            this.LOGGER.error("InsertSig: Have no signature field");
            throw new DocumentException("No field");
        }
        if (!af.signatureCoversWholeDocument(signField)) {
            this.LOGGER.error("InsertSig: Not the last signature");
            throw new DocumentException("Not the last signature");
        }
        PdfArray b = v.getAsArray(PdfName.BYTERANGE);
        long[] gaps = b.asLongArray();
        if ((b.size() != 4) || (gaps[0] != 0L)) {
            this.LOGGER.error("InsertSig: Single exclusion space supported");
            throw new DocumentException("Single exclusion space supported");
        }
        RandomAccessSource readerSource = reader.getSafeFile().createSourceView();

        String hashAlgorithm = this.hashAlg;
        BouncyCastleDigest digest = new BouncyCastleDigest();
        PdfPKCS7 sgn = new PdfPKCS7(null, chain, hashAlgorithm, null, digest, false);
        sgn.setExternalDigest(extSignature, null, "RSA");
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(signDate);
        byte[] signedContent = sgn.getEncodedPKCS7(hash, getSigningDate(signDate), null, null, null, MakeSignature.CryptoStandard.CMS);
        int spaceAvailable = (int) (gaps[2] - gaps[1]) - 2;
        if ((spaceAvailable & 0x1) != 0) {
            this.LOGGER.error("InsertSig: Gap is not a multiple of 2");
            throw new DocumentException("Gap is not a multiple of 2");
        }
        spaceAvailable /= 2;
        if (spaceAvailable < signedContent.length) {
            this.LOGGER.error("InsertSig: Not enough space");
            throw new DocumentException("Not enough space");
        }
        StreamUtil.CopyBytes(readerSource, 0L, gaps[1] + 1L, os);
        ByteBuffer bb = new ByteBuffer(spaceAvailable * 2);
        for (byte bi : signedContent) {
            bb.appendHex(bi);
        }
        int remain = (spaceAvailable - signedContent.length) * 2;
        for (int k = 0; k < remain; k++) {
            bb.append((byte) 48);
        }
        bb.writeTo(os);
        StreamUtil.CopyBytes(readerSource, gaps[2] - 1L, gaps[3] + 1L, os);
        os.close();
        bb.close();
    }

    private String getFontURLFromResource() {
        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResource("times.ttf").toString();
    }

    private void emptySignature(byte[] content, String dest, String fieldName, X509Certificate cert,
                                String reason, String location, String signerLabel, String signDateLabel,
                                int pageNum, int visibleWidth, int visibleHeight, int visibleX, int visibleY, Date signDate) throws Exception {
        BouncyCastleProvider providerBC = new BouncyCastleProvider();
        Security.addProvider(providerBC);
        PdfReader.unethicalreading = true;
        PdfReader reader = new PdfReader(content);
        FileOutputStream os = new FileOutputStream(dest);
        PdfStamper stamper = PdfStamper.createSignature(reader, os, '\000', null, true);
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setSignDate(getSigningDate(signDate));
        appearance.setVisibleSignature(new Rectangle(visibleX, visibleY, visibleX + visibleWidth, visibleY + visibleHeight), pageNum, fieldName);
        BaseFont signatureBaseFont = BaseFont.createFont(getFontURLFromResource(), "Identity-H", false);
        float fontSize = 10.0F;
        Font regularFont = new Font(signatureBaseFont, fontSize);
        regularFont.setColor(new BaseColor(255, 0, 0));
        appearance.setLayer2Font(regularFont);
        String cnName = getCN(cert);
        String addtional = "";

        appearance.setLayer2Text(signerLabel + ": " + cnName + "\n" + signDateLabel + ": " +
                DatetimeUtils.convertDateToString(signDate, "DD/MM/YYYY") + addtional);

        ExternalSignatureContainer external = new ExternalBlankSignatureContainer(PdfName.ADOBE_PPKLITE, PdfName.ADBE_PKCS7_DETACHED);
        MakeSignature.signExternalContainer(appearance, external, 8192);
        try {
            os.close();
        } catch (Exception localException) {
        }
    }

    private String getCN(X509Certificate cert) throws Exception {
//        String DN = cert.getSubjectDN().getName();
//        String[] nameArr = DN.split(",");
//        for (String name : nameArr) {
//            name = name.trim();
//            if (name.toLowerCase().indexOf("cn") == 0) {
//                return name.substring(name.indexOf("=") + 1);
//            }
//        }
//        return DN;
        X500Name x500name = new JcaX509CertificateHolder(cert).getSubject();
        RDN cn = x500name.getRDNs(BCStyle.CN)[0];

        return IETFUtils.valueToString(cn.getFirst().getValue());
    }

    private List<byte[]> preSign(String src, String fieldName, Certificate[] chain, Date signDate)
            throws Exception {
        List<byte[]> result = new ArrayList();
        PdfReader reader = new PdfReader(src);
        AcroFields af = reader.getAcroFields();
        PdfDictionary v = af.getSignatureDictionary(fieldName);
        if (v == null) {
            this.LOGGER.error("PreSign: Have no signature field");
            throw new DocumentException("No field");
        }
        PdfArray b = v.getAsArray(PdfName.BYTERANGE);
        long[] gaps = b.asLongArray();
        if ((b.size() != 4) || (gaps[0] != 0L)) {
            this.LOGGER.error("PreSign: Single exclusion space supported");
            throw new DocumentException("PreSign: Single exclusion space supported");
        }
        RandomAccessSource readerSource = reader.getSafeFile().createSourceView();
        InputStream rg = new RASInputStream(new RandomAccessSourceFactory().createRanged(readerSource, gaps));
        BouncyCastleDigest digest = new BouncyCastleDigest();
        PdfPKCS7 sgn = new PdfPKCS7(null, chain, this.hashAlg, null, digest, false);
        byte[] hash = DigestAlgorithms.digest(rg, digest.getMessageDigest(this.hashAlg));
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(signDate);
        byte[] sh = sgn.getAuthenticatedAttributeBytes(hash, getSigningDate(signDate), null, null, MakeSignature.CryptoStandard.CMS);

        byte[] toSign = MessageDigest.getInstance("SHA-1").digest(sh);

        result.add(toSign);
        result.add(hash);
        return result;
    }

    private Calendar getSigningDate(Date date) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String timezoneSinging = "Asia/Bangkok";
        String signTime = dateFormat.format(date);
        return DatetimeUtils.convertStringToDate(signTime, "DD/MM/YYYY HH:MI:SS", timezoneSinging);
    }
}
