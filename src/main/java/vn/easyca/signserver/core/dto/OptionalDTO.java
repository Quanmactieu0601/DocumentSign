package vn.easyca.signserver.core.dto;

import vn.easyca.signserver.pki.sign.utils.StringUtils;
import vn.easyca.signserver.webapp.config.Constants;

public class OptionalDTO {
    private String hashAlgorithm = Constants.HASH_ALGORITHM.DEFAULT_HASH_ALGORITHM;
    private String signatureAlgorithm = "SHA1WithRSA";

    private boolean returnInputData;

    public String getHashAlgorithm() {
        return StringUtils.isNullOrEmpty(hashAlgorithm) ? Constants.HASH_ALGORITHM.DEFAULT_HASH_ALGORITHM : hashAlgorithm;
    }

    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
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
}
