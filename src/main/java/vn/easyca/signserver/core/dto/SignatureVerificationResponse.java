package vn.easyca.signserver.core.dto;

import java.util.ArrayList;
import java.util.List;

public class SignatureVerificationResponse {

    private String certificate;

    private final List<Element> elements = new ArrayList<>();

    public List<Element> getElements() {
        return elements;
    }

    public void add(String key, boolean result) {
        elements.add(new Element(key, result));
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public static class Element {
        private String key;
        private boolean result;

        public Element(String key, boolean result) {
            this.key = key;
            this.result = result;
        }

        public String getKey() {
            return key;
        }

        public boolean getResult() {
            return result;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public void setResult(boolean result) {
            this.result = result;
        }
    }
}
