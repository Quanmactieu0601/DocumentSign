package vn.easyca.signserver.webapp.service;

import java.security.NoSuchAlgorithmException;

public interface Encryption {


    public String encrypt(String data) throws EncryptionException;

    public String decrypt(String data) throws EncryptionException;

    public class EncryptionException extends Exception{
        public EncryptionException(String message) {
            super(message);
        }
    }
}
