package vn.easyca.signserver.core.dto;

import vn.easyca.signserver.pki.sign.utils.StringUtils;
import vn.easyca.signserver.webapp.config.Constants;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class SignatureVerificationRequest {

    private String serial;
    private String hashAlgorithm;
    private List<Element> elements = new ArrayList<>();

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    public String getHashAlgorithm() {
        return StringUtils.isNullOrEmpty(hashAlgorithm) ? Constants.HASH_ALGORITHM.DEFAULT_HASH_ALGORITHM : hashAlgorithm;
    }

    public void setHashAlgorithm(String hashAlgorithm) {
        if (hashAlgorithm == null || hashAlgorithm.isEmpty())
            return;
        this.hashAlgorithm = hashAlgorithm;
    }

    public void add(Element element) {
        elements.add(element);
    }

    public List<Element> getElements() {
        return elements;
    }


    public static class Element {
        public Element(String key, String base64Signature, String base64OriginalData) {
            this.key = key;
            this.base64Signature = base64Signature;
            this.base64OriginalData = base64OriginalData;
        }

        private String key;
        private String base64Signature;
        private String base64OriginalData;

        public Element() {
        }

        public byte[] getSignature() {
            return Base64.getDecoder().decode(base64Signature);
        }

        public byte[] getOriginalData() {
            return Base64.getDecoder().decode(base64OriginalData);
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getBase64Signature() {
            return base64Signature;
        }

        public void setBase64Signature(String base64Signature) {
            this.base64Signature = base64Signature;
        }

        public String getBase64OriginalData() {
            return base64OriginalData;
        }

        public void setBase64OriginalData(String base64OriginalData) {
            this.base64OriginalData = base64OriginalData;
        }
    }

}
