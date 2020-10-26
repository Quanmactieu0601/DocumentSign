package vn.easyca.signserver.webapp.web.rest.vm.request;

import vn.easyca.signserver.core.dto.SignatureVerificationRequest;
import vn.easyca.signserver.core.utils.CommonUtils;
import vn.easyca.signserver.webapp.web.rest.mapper.SignatureVerificationRequestMapper;

import java.security.cert.X509Certificate;
import java.util.List;

public class SignatureVerificationVM {

    private String hashAlgorithm;

    private List<ElementVM> elements;

    private String serial;

    private String base64Certificate;

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    public List<ElementVM> getElements() {
        return elements;
    }

    public void setElements(List<ElementVM> elements) {
        this.elements = elements;
    }

    public String getSerial() {
        if (serial != null && !serial.isEmpty())
            return serial;
        try {
            if (base64Certificate != null && !base64Certificate.isEmpty()) {
                X509Certificate certificate = CommonUtils.decodeBase64X509(base64Certificate);
                return certificate.getSerialNumber().toString(16);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public SignatureVerificationRequest mapToDTO() {
        return new SignatureVerificationRequestMapper().map(this);
    }

    public String getBase64Certificate() {
        return base64Certificate;
    }

    public void setBase64Certificate(String base64Certificate) {
        this.base64Certificate = base64Certificate;
    }

    public static class ElementVM {
        private String key;
        private String base64Signature;
        private String base64OriginalData;

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
