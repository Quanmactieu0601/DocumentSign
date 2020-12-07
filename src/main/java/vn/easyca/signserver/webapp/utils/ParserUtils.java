package vn.easyca.signserver.webapp.utils;

import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.swing.Java2DRenderer;
import vn.easyca.signserver.core.domain.CertificateDTO;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.core.utils.CommonUtils;
import vn.easyca.signserver.webapp.domain.SignatureTemplate;
import vn.easyca.signserver.webapp.service.dto.SignatureImageDTO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserUtils {
    public static String getElementContentNameInCertificate(String contentInformation, String regex) {

        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(contentInformation);
        String CN = null;
        while (matcher.find()) {
            CN = matcher.group(1);
        }
        return CN;
    }



    public static String convertHtmlContentToBase64(String htmlContent) throws ApplicationException {
        //Read it using Utf-8 - Based on encoding, change the encoding name if you know it
        try {
            InputStream htmlStream = new ByteArrayInputStream(htmlContent.getBytes("UTF-8"));
            Tidy tidy = new Tidy();
            org.w3c.dom.Document doc = tidy.parseDOM(new InputStreamReader(htmlStream, "UTF-8"), null);

            Java2DRenderer renderer = new Java2DRenderer(doc, 400, 150);
            BufferedImage img = renderer.getImage();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(img, "png", os);
            return Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (UnsupportedEncodingException e) {
            throw new ApplicationException("Error encoding when convert html to base64", e);
        } catch (IOException e) {
            throw new ApplicationException("Error I/O when convert html to base64", e);
        }
    }

    public static String getHtmlTemplateAndSignData(String subjectDN, String signatureTemplate, String signatureImage) {
        //todo: hiện tại chỉ đang lấy pattern theo khách hàng Quốc Dũng như này còn khách hàng khác xử lý sau
        final String regexCN = "CN=\"([^\"]+)\"";
        final String regexT = ", T=([^,]+)";
        String CN = ParserUtils.getElementContentNameInCertificate(subjectDN, regexCN);
        String T = ParserUtils.getElementContentNameInCertificate(subjectDN, regexT);
        String[] signerInfor = CN.split(",");
        String signerName = signerInfor[0];
        String address = signerInfor[1];
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss, dd/MM/yyyy", Locale.getDefault());
        Calendar cal = Calendar.getInstance();

        String htmlContent = signatureTemplate;
        htmlContent = htmlContent
            .replaceFirst("signer", signerName)
            .replaceFirst("position", T)
            .replaceFirst("address", address)
            .replaceFirst("signatureImage", signatureImage)
            .replaceFirst("timeSign", dateFormat.format(cal.getTime()));
        return htmlContent;
    }
}
