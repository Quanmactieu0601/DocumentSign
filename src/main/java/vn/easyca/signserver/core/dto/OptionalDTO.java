package vn.easyca.signserver.core.dto;

import vn.easyca.signserver.pki.sign.utils.StringUtils;
import vn.easyca.signserver.webapp.config.Constants;

public class OptionalDTO {
    private String hashAlgorithm = Constants.HASH_ALGORITHM.DEFAULT_HASH_ALGORITHM;
    private String signatureAlgorithm = "SHA1withRSA";

    private boolean returnInputData;

    public String getHashAlgorithm() {
        return StringUtils.isNullOrEmpty(hashAlgorithm) ? Constants.HASH_ALGORITHM.DEFAULT_HASH_ALGORITHM : hashAlgorithm;
    }

    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    public String getSignatureAlgorithm() {
        if (!StringUtils.isNullOrEmpty(signatureAlgorithm))
            return signatureAlgorithm;
        String hashAlgorithm = getHashAlgorithm();
        return hashAlgorithm + "withRSA";
    }

    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    public void setReturnInputData(boolean returnInputData) {
        this.returnInputData = returnInputData;
    }

    public boolean isReturnInputData() {
        return returnInputData;
    }

    @Override
    public String toString() {
        return "OptionalDTO{" +
            "hashAlgorithm='" + hashAlgorithm + '\'' +
            ", signatureAlgorithm='" + signatureAlgorithm + '\'' +
            ", returnInputData=" + returnInputData +
            '}';
    }
}
