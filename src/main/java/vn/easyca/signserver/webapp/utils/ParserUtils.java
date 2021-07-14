package vn.easyca.signserver.webapp.utils;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

import org.apache.commons.io.IOUtils;
import org.springframework.core.env.Environment;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.swing.Java2DRenderer;
import vn.easyca.signserver.core.exception.ApplicationException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserUtils {
    public static String getElementContentNameInCertificate(String contentInformation, String regex) throws ApplicationException {
        try {
            final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            final Matcher matcher = pattern.matcher(contentInformation);
            String CN = null;
            while (matcher.find()) {
                CN = matcher.group(1);
            }
            return CN;
        } catch (Exception ex) {
            throw new ApplicationException(String.format("Parse CN has error: [content: %s, regex: %s]", contentInformation, regex), ex);
        }
    }

    public static String convertHtmlContentToBase64(String htmlContent) throws ApplicationException {
        //Read it using Utf-8 - Based on encoding, change the encoding name if you know it
        try {
            InputStream htmlStream = new ByteArrayInputStream(htmlContent.getBytes("UTF-8"));
            Tidy tidy = new Tidy();
            org.w3c.dom.Document doc = tidy.parseDOM(new InputStreamReader(htmlStream, "UTF-8"), null);
            Java2DRenderer renderer = new Java2DRenderer(doc, 355, 130);
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

    public static String convertHtmlContentToBase64Resize(String htmlContent, Integer width, Integer height, boolean transparency) throws ApplicationException {
        //Read it using Utf-8 - Based on encoding, change the encoding name if you know it
        try {
            InputStream htmlStream = new ByteArrayInputStream(htmlContent.getBytes("UTF-8"));
            Tidy tidy = new Tidy();
            org.w3c.dom.Document doc = tidy.parseDOM(new InputStreamReader(htmlStream, "UTF-8"), null);
            Java2DRenderer renderer;
            if (transparency) {
                final java.awt.Color TRANSPARENT = new Color(255, 255, 255, 0);
                final int imageType = BufferedImage.TYPE_INT_ARGB;
                renderer = new Java2DRenderer(doc, width, height) {
                    @Override
                    protected BufferedImage createBufferedImage(final int width, final int height) {
                        final BufferedImage image = org.xhtmlrenderer.util.ImageUtil.createCompatibleBufferedImage(width, height, imageType);
                        org.xhtmlrenderer.util.ImageUtil.clearImage(image, TRANSPARENT);
                        return image;
                    }
                };
                renderer.setBufferedImageType(imageType);
            } else {
                renderer = new Java2DRenderer(doc, width, height);
            }

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


    public static String convertHtmlContentToImageByProversion(String htmlContent, Integer width, Integer height, boolean transparency, Environment env) throws ApplicationException {
        String pathProject = env.getProperty("spring.servlet.multipart.location");

        String unique = UUID.randomUUID().toString();
        String fileInputHtml = unique + ".html";
        String fileOutputImage = unique + ".png";

        try {
            String content = new String(htmlContent.getBytes());
            FileWriter fw = new FileWriter(pathProject + "/" + fileInputHtml, true); //the true will append the new data
            fw.write(content);//appends the string to the file
            fw.close();

            String fileInputPath = pathProject + "/" + fileInputHtml;
            String fileOutputPath = pathProject + "/" + fileOutputImage;
            CommandLine cmdLine = null;


//            String os = System.getProperty("os.name");
//            if (os.startsWith("Windows")) {
//                cmdLine = transparency ? CommandLine.parse("cmd /c wkhtmltoimage --crop-h " + height +" --crop-w " + width + " --transparent " + " --quality 80 -f png " + fileInputPath + " " + fileOutputPath)
//                                        :CommandLine.parse("cmd /c wkhtmltoimage --crop-h " + height +" --crop-w " + width + " --quality 80 -f png " + fileInputPath + " " + fileOutputPath);
//            } else {
//                cmdLine = transparency ? CommandLine.parse("wkhtmltoimage --crop-h " + height +" --crop-w " + width + " --transparent " + " --quality 80 -f png " + fileInputPath + " " + fileOutputPath)
//                                        :CommandLine.parse("wkhtmltoimage --crop-h " + height +" --crop-w " + width + " --quality 80 -f png " + fileInputPath + " " + fileOutputPath);
//            }

            String os = System.getProperty("os.name");
            String prefixCommand = "";
            if (os.startsWith("Windows")) {
                prefixCommand = "cmd /c ";
            } else {
                prefixCommand = "";
            }

            String command = String.format("%s wkhtmltoimage --crop-h %s --crop-w %s  %s --quality %s -f png  %s %s",
                prefixCommand, height, width, transparency ? " --transparent " : "", 80, fileInputPath, fileOutputPath);
            cmdLine = CommandLine.parse(command);


            DefaultExecutor executor = new DefaultExecutor();

            // run command line
            int exitValue = executor.execute(cmdLine);
            Files.deleteIfExists(Paths.get(fileInputPath));

            // get content image
            byte[] imageContent = Files.readAllBytes(Paths.get(fileOutputPath));
            String imageContentExport = Base64.getEncoder().encodeToString(imageContent);
            Files.deleteIfExists(Paths.get(fileOutputPath));

            return imageContentExport;
        } catch (IOException ioe) {
            throw new ApplicationException("wkhtmltoimage - Convert html to image error: ", ioe);
        }
    }
}
