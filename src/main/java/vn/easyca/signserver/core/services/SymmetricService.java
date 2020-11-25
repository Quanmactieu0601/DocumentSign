package vn.easyca.signserver.core.services;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.pki.sign.utils.StringUtils;
import vn.easyca.signserver.webapp.config.ApplicationProperties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

@Component
public class SymmetricService {

    private final ApplicationProperties properties;

    public SymmetricService(ApplicationProperties properties) {
        this.properties = properties;
    }

    public String encrypt(String rawData) throws ApplicationException {
        byte[] data = rawData.getBytes();
        byte[] result = execute(data, Cipher.ENCRYPT_MODE);
        return Base64.getMimeEncoder().encodeToString(result);
    }

    public String decrypt(String encryptedData) throws ApplicationException {
        byte[] data = Base64.getDecoder().decode(encryptedData);
        byte[] result = execute(data, Cipher.DECRYPT_MODE);
        return new String(result);
    }

    @NotNull
    private byte[] execute(byte[] data, int decryptMode) throws ApplicationException {
        String symmetricKey = properties.getSymmetricKey();
        if (StringUtils.isNullOrEmpty(symmetricKey))
            throw new ApplicationException(ApplicationException.BAD_INPUT_ERROR_CODE, "Khóa mã hóa đối xứng trống, vui lòng thử lại");
        try {
            byte[] key = symmetricKey.getBytes();
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            Cipher c = Cipher.getInstance("AES");
            SecretKeySpec k =
                new SecretKeySpec(key, "AES");
            c.init(decryptMode, k);
            return c.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
            throw new ApplicationException(ApplicationException.APPLICATION_ERROR_CODE, "NoSuchAlgorithmException", e);
        } catch (InvalidKeyException e) {
            throw new ApplicationException(ApplicationException.APPLICATION_ERROR_CODE, "InvalidKeyException", e);
        } catch (NoSuchPaddingException e) {
            throw new ApplicationException(ApplicationException.APPLICATION_ERROR_CODE, "NoSuchPaddingException", e);
        } catch (BadPaddingException e) {
            throw new ApplicationException(ApplicationException.APPLICATION_ERROR_CODE, "BadPaddingException", e);
        } catch (IllegalBlockSizeException e) {
            throw new ApplicationException(ApplicationException.APPLICATION_ERROR_CODE, "IllegalBlockSizeException", e);
        }
    }
}
