package vn.easyca.signserver.pki.sign.integrated.pdf.visible;

import com.itextpdf.text.*;
import com.itextpdf.text.io.RASInputStream;
import com.itextpdf.text.io.RandomAccessSource;
import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.io.StreamUtil;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.security.*;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.easyca.signserver.pki.sign.utils.DatetimeUtils;
import vn.easyca.signserver.pki.sign.utils.StringUtils;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class SignPDFLib {
    private final Logger LOGGER = LoggerFactory.getLogger(SignPDFLib.class);
    private String hashAlg = "SHA1";
    private PartyMode partyMode = PartyMode.CRM_CONTRACT;
    private static final String DEFAULT_BG ="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";
    public void setHashAlg(String hashAlg) {
        this.hashAlg = hashAlg;
    }

    public SignPDFLib() {
    }

    public SignPDFLib(String hashAlg, PartyMode partyMode) {
        this.hashAlg = hashAlg;
        this.partyMode = partyMode;
    }
//    public List<byte[]> createHash(byte[] content, String tempFile, Certificate[] chain, JSONObject signature_info,
//                                   JSONObject visible_signature, Date signDate, String signField) throws Exception {
//
//        SignPDFDto signPDFDto = SignPDFDto.build(partyMode,content,null,chain,tempFile);
//        if (signature_info.has("reason")) {
//            si signature_info.getString("reason");
//        }
//        String location = "";
//        if (signature_info.has("location")) {
//            location = signature_info.getString("location");
//        }
//        String signerLabel = "Ký bởi";
//        if (signature_info.has("signerLabel")) {
//            signerLabel = signature_info.getString("signerLabel");
//        }
//        String signDateLabel = "Ký ngày";
//        if (signature_info.has("signDateLabel")) {
//            signDateLabel = signature_info.getString("signDateLabel");
//        }
//        int pageNum = 11;
//        if (visible_signature.has("pageNum")) {
//            pageNum = ((Integer) visible_signature.get("pageNum")).intValue();
//        }
//        int visibleX = 0;
//        if (visible_signature.has("visibleX")) {
//            visibleX = ((Integer) visible_signature.get("visibleX")).intValue();
//        }
//        int visibleY = 0;
//        if (visible_signature.has("visibleY")) {
//            visibleY = ((Integer) visible_signature.get("visibleY")).intValue();
//        }
//        int visibleWidth = 150;
//        if (visible_signature.has("visibleWidth")) {
//            visibleWidth = ((Integer) visible_signature.get("visibleWidth")).intValue();
//        }
//        int visibleHeight = 50;
//        if (visible_signature.has("visibleHeight")) {
//            visibleHeight = ((Integer) visible_signature.get("visibleHeight")).intValue();
//        }
//        if ((pageNum < 1) || (visibleX < 0) || (visibleY < 0) || (visibleWidth < 0) || (visibleHeight < 0)) {
//            throw new Exception("Dữ liệu định dạng khung ký sai.");
//        }
//        signPDFDto.setPageNumber(pageNum);
//        signPDFDto.setLocation();
//        emptySignature(signPDFDto);
//
//        return preSign(tempFile, signField, chain, signDate);
//    }
//
    public List<byte[]> createHash(SignPDFDto dto, String tempFile) throws Exception {
        this.partyMode = dto.getPartyMode();
        this.hashAlg = StringUtils.isNullOrEmpty(dto.getHashAlg()) ? this.hashAlg : dto.getHashAlg();
        validDTO(dto);
        emptySignature(dto,tempFile);
        return preSign(tempFile,dto.getSignField(),dto.getChain(),dto.getSignDate());
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
        byte[] signedContent = sgn.getEncodedPKCS7(hash, getSigningDate(signDate), null, null, null, CryptoStandard.CMS);
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

    private void emptySignature(SignPDFDto signDTO,String tempFile) throws Exception {
        BouncyCastleProvider providerBC = new BouncyCastleProvider();
        Security.addProvider(providerBC);
        PdfReader.unethicalreading = true;
        PdfReader reader = new PdfReader(signDTO.getContent());
        FileOutputStream os = new FileOutputStream(tempFile);
        PdfStamper stamper = PdfStamper.createSignature(reader, os, '\000', null, true);
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setSignDate(getSigningDate(signDTO.getSignDate()));
        Rectangle rectangle = new Rectangle(signDTO.getVisibleX(), signDTO.getVisibleY(), signDTO.getVisibleX() + signDTO.getVisibleWidth(), signDTO.getVisibleY() + signDTO.getVisibleWidth());
        appearance.setVisibleSignature(rectangle, signDTO.getPageNumber(), signDTO.getSignField());
        BaseFont signatureBaseFont = BaseFont.createFont(getFontURLFromResource(), "Identity-H", false);
        float fontSize = 10.0F;
        Font regularFont = new Font(signatureBaseFont, fontSize);
        regularFont.setColor(new BaseColor(255, 0, 0));
        appearance.setLayer2Font(regularFont);
        String cnName = getCN((X509Certificate) signDTO.getFirstCert());
        String addtional = "";
        String layer2Text = "";
        if (this.partyMode == PartyMode.CRM_CONTRACT) {
            layer2Text = signDTO.getSignerLabel() + ": " + cnName + "\n" + signDTO.getSignDateLabel() + ": " +
                DatetimeUtils.convertDateToString(signDTO.getSignDate(), "DD/MM/YYYY") + addtional;
        }

        if (this.partyMode == PartyMode.CA_ATTACHMENT) {
            layer2Text = signDTO.getReason() + "\n\n" + signDTO.getSignerLabel() + "\n" + signDTO.getSignDateLabel() + ": " +
                DatetimeUtils.convertDateToString(signDTO.getSignDate(), "DD/MM/YYYY") + addtional;
        }
        if (this.partyMode == PartyMode.SIGN_SERVER) {
            String signer =  StringUtils.isNullOrEmpty(signDTO.getSigner()) ? cnName : signDTO.getSigner();
            layer2Text = signDTO.getReason() + "\n\n" + signDTO.getSignerLabel()+ ": " + signer + "\n" +  signDTO.getSignDateLabel() + ": " +
                DatetimeUtils.convertDateToString(signDTO.getSignDate(), "DD/MM/YYYY") + addtional;
        }

        float MARGIN = 5;
        Rectangle dataRec = new Rectangle(MARGIN, MARGIN, appearance.getRect().getWidth(), appearance.getRect().getHeight());
        ColumnText ct = new ColumnText(appearance.getLayer(2));
        ct.setRunDirection(appearance.getRunDirection());
        ct.setSimpleColumn(new Phrase(layer2Text, regularFont), dataRec.getLeft(), dataRec.getBottom(), dataRec.getRight(), dataRec.getTop(), fontSize, Element.ALIGN_LEFT);
        ct.go();

        PdfTemplate layer20 = appearance.getLayer(2);
        Image bg = Image.getInstance(Base64.getDecoder().decode(DEFAULT_BG));
        bg.scaleToFit(50,35);
        bg.setAbsolutePosition(2,30);
        layer20.addImage(bg);
        rectangle = appearance.getRect();
        layer20.setLineWidth(1);
        layer20.setRGBColorStroke(255, 0, 0);
        layer20.rectangle(rectangle.getLeft(), rectangle.getBottom(), rectangle.getWidth(), rectangle.getHeight());
        layer20.stroke();

        ExternalSignatureContainer external = new ExternalBlankSignatureContainer(PdfName.ADOBE_PPKLITE, PdfName.ADBE_PKCS7_DETACHED);
        MakeSignature.signExternalContainer(appearance, external, 8192);
        try {
            os.close();
        } catch (Exception localException) {
        }
    }

    private String getCN(X509Certificate cert) throws Exception {
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
        byte[] sh = sgn.getAuthenticatedAttributeBytes(hash, getSigningDate(signDate), null, null, CryptoStandard.CMS);

        byte[] toSign = MessageDigest.getInstance("SHA-1").digest(sh);

        result.add(toSign);
        result.add(hash);
        return result;
    }

    private Calendar getSigningDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String timezoneSinging = "Asia/Bangkok";
        String signTime = dateFormat.format(date);
        return DatetimeUtils.convertStringToDate(signTime, "DD/MM/YYYY HH:MI:SS", timezoneSinging);
    }

    private void validDTO(SignPDFDto dto) throws Exception {

        if ((dto.getPageNumber() < 1) ||
            (dto.getVisibleX() < 0) || (dto.getVisibleY() < 0) ||
            (dto.getVisibleWidth() < 0) || (dto.getVisibleHeight() < 0))
            throw new Exception("Dữ liệu định dạng khung ký sai.");

    }
}
