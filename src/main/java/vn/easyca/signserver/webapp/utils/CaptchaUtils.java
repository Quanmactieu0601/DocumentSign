package vn.easyca.signserver.webapp.utils;

import org.springframework.stereotype.Component;
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
//        CaptchaUtils captchaUtils = new CaptchaUtils();
//        CaptchaDTO captchaDTO = captchaUtils.generateCaptcha();
//        System.out.println(captchaDTO.getCaptchaText());
//        System.out.println(captchaDTO.getCaptchaImg());
//    }

    public static CaptchaDTO generateCaptcha() throws NoSuchAlgorithmException, IOException {
        String text = randomCaptchaText();
        String captchaText = hashCaptchaText(text);
        String captchaImg = drawCaptchaImgFromText(text);
        return new CaptchaDTO(captchaText, captchaImg);
    }

    public static String hashCaptchaText(String text) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] result = md.digest(text.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static String randomCaptchaText(){
        String text="qwertyuiopasdfghjklzxcvbnm1234567890";
        Random random = new Random();
        String captchaText = "";
        for (int i=0; i<4; i++){
            captchaText += text.charAt(random.nextInt(text.length()));
        }
        return captchaText;
    }

    public static String drawCaptchaImgFromText(String text) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(100, 40, Transparency.OPAQUE);
        Graphics graphics = bufferedImage.createGraphics();
        graphics.setFont(new Font("Brush Script MT", Font.BOLD, 35));
        graphics.setColor(new Color(0, 0, 255));
        graphics.fillRect(0, 0, 100, 40);
        graphics.setColor(new Color(255, 255, 255));
        graphics.drawString(text, 20, 25);
        graphics.dispose();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        String captchaImage = "data:image/png;base64," + DatatypeConverter.printBase64Binary(byteArrayOutputStream.toByteArray());
        return captchaImage;
    }
}
