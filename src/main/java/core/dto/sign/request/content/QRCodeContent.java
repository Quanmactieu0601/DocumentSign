package core.dto.sign.request.content;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import study.web.rest.SigningResource;

public class QRCodeContent {

    private byte[] data;
    private static final Logger log = LoggerFactory.getLogger(SigningResource.class);

    public QRCodeContent(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String creatHashData() {
        log.info(" --- create Hash Data --- ");
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
            log.info(" --- fail to create Hash Data --- ");
        }
        byte[] hash = digest.digest(this.data);
        String rs = DatatypeConverter.printHexBinary(hash);
        return rs;
    }

    public String toString() {
        return creatHashData();
    }
}
