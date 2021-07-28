package vn.easyca.signserver.webapp.utils;

import sun.awt.Win32GraphicsEnvironment;
import sun.java2d.HeadlessGraphicsEnvironment;
import vn.easyca.signserver.webapp.service.dto.CaptchaDTO;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class CaptchaUtils {

//    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
//        HeadlessGraphicsEnvironment headlessGraphicsEnvironment = new HeadlessGraphicsEnvironment(new Win32GraphicsEnvironment());
//        String[] name = headlessGraphicsEnvironment.getAvailableFontFamilyNames();
//        for (String n: name) {
//            System.out.println(n);
//        }
//    }

    public static CaptchaDTO generateCaptcha() throws NoSuchAlgorithmException, IOException {
        String text = randomCaptchaText();
        String captchaText = hashCaptchaText(text);
        String captchaImg = drawCaptchaImgFromText(text);
        System.out.println(captchaImg);
        return new CaptchaDTO(captchaText, captchaImg);
    }

    public static String hashCaptchaText(String text) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] result = md.digest(text.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : result) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static String randomCaptchaText(){
        String text="qwertyuiopasdfghjklzxcvbnm1234567890";
        Random random = new Random();
        StringBuilder captchaText = new StringBuilder();
        for (int i=0; i<4; i++){
            captchaText.append(text.charAt(random.nextInt(text.length())));
        }
        return captchaText.toString();
    }

    public static String drawCaptchaImgFromText(String text) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(100, 40, Transparency.OPAQUE);
        Graphics graphics = bufferedImage.createGraphics();
        graphics.setFont(new Font("MS Mincho", Font.BOLD, 28));
        graphics.setColor(new Color(96, 147, 172));
        graphics.fillRect(0, 0, 100, 40);
        graphics.setColor(new Color(10, 10, 10));
        graphics.drawString(text, 10, 28);
        graphics.dispose();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        return "data:image/png;base64," + DatatypeConverter.printBase64Binary(byteArrayOutputStream.toByteArray());
    }
}
