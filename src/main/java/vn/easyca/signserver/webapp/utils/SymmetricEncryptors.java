package vn.easyca.signserver.webapp.utils;

import org.springframework.stereotype.Component;
import vn.easyca.signserver.core.exception.ApplicationException;
import vn.easyca.signserver.webapp.config.ApplicationProperties;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


@Component
public class SymmetricEncryptors {
    private static final int KEY_LENGTH = 16;
    private final ApplicationProperties properties;

    public SymmetricEncryptors(ApplicationProperties properties) {
        this.properties = properties;
    }

    public String encrypt(String data) throws ApplicationException {
        try {
            byte[] key = Base64.getDecoder().decode(properties.getSymmetricKey());
            byte[] clean = data.getBytes();

            // Generating IV.
            int ivSize = KEY_LENGTH;
            byte[] iv = new byte[ivSize];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            // Hashing key.
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(key);
            byte[] keyBytes = new byte[16];
            System.arraycopy(digest.digest(), 0, keyBytes, 0, keyBytes.length);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

            // Encrypt.
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(clean);

            // Combine IV and encrypted part.
            byte[] encryptedIVAndText = new byte[ivSize + encrypted.length];
            System.arraycopy(iv, 0, encryptedIVAndText, 0, ivSize);
            System.arraycopy(encrypted, 0, encryptedIVAndText, ivSize, encrypted.length);
            return Base64.getEncoder().encodeToString(encryptedIVAndText);
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
        } catch (InvalidAlgorithmParameterException e) {
            throw new ApplicationException(ApplicationException.APPLICATION_ERROR_CODE, "InvalidAlgorithmParameterException", e);
        }
    }

    public String decrypt(String data) throws ApplicationException {
        try {
            byte[] key = Base64.getDecoder().decode(properties.getSymmetricKey());
            int ivSize = KEY_LENGTH;
            int keySize = KEY_LENGTH;
            byte[] encryptedIvTextBytes = Base64.getDecoder().decode(data);
            // Extract IV.
            byte[] iv = new byte[ivSize];
            System.arraycopy(encryptedIvTextBytes, 0, iv, 0, iv.length);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            // Extract encrypted part.
            int encryptedSize = encryptedIvTextBytes.length - ivSize;
            byte[] encryptedBytes = new byte[encryptedSize];
            System.arraycopy(encryptedIvTextBytes, ivSize, encryptedBytes, 0, encryptedSize);

            // Hash key.
            byte[] keyBytes = new byte[keySize];
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(key);
            System.arraycopy(md.digest(), 0, keyBytes, 0, keyBytes.length);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

            // Decrypt.
            Cipher cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] decrypted = cipherDecrypt.doFinal(encryptedBytes);
            return new String(decrypted);
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
        } catch (InvalidAlgorithmParameterException e) {
            throw new ApplicationException(ApplicationException.APPLICATION_ERROR_CODE, "InvalidAlgorithmParameterException", e);
        }
    }
}
