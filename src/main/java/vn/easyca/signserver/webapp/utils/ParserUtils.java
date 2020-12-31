package vn.easyca.signserver.webapp.utils;

import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.swing.Java2DRenderer;
import vn.easyca.signserver.core.exception.ApplicationException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
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
}
