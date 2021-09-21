package vn.easyca.signserver.core.dto.sign.request.content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.easyca.signserver.webapp.web.rest.SigningResource;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class QRCodeContent {
    private String data;
    private static final Logger log = LoggerFactory.getLogger(SigningResource.class);

    public QRCodeContent(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
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
        byte[] hash = digest.digest(data.toString().getBytes(StandardCharsets.UTF_8));
        String rs = DatatypeConverter.printHexBinary(hash);
        return rs ;
    }

    public String toString(){
        return creatHashData();
    }
}
