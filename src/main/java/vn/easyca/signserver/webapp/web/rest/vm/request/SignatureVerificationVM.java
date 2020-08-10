package vn.easyca.signserver.webapp.web.rest.vm.request;

import vn.easyca.signserver.business.services.sign.dto.request.SignatureVerificationRequest;
import vn.easyca.signserver.webapp.web.rest.mapper.SignatureVerificationRequestMapper;

import java.util.List;

public class SignatureVerificationVM {

    private String hashAlgorithm;

    private List<ElementVM> elements;

    private String serial;

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
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public SignatureVerificationRequest mapToDTO(){
        return new SignatureVerificationRequestMapper().map(this);
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
