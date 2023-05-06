package core.dto;

import pki.sign.utils.StringUtils;
import study.config.Constants;
import study.web.rest.vm.request.sign.OptionalVM;

public class OptionalDTO extends OptionalVM {

    private String hashAlgorithm = Constants.HASH_ALGORITHM.DEFAULT_HASH_ALGORITHM;
    private String signatureAlgorithm = "SHA1withRSA";
    private String otpCode;

    private boolean returnInputData;

    public String getHashAlgorithm() {
        return StringUtils.isNullOrEmpty(hashAlgorithm) ? Constants.HASH_ALGORITHM.DEFAULT_HASH_ALGORITHM : hashAlgorithm;
    }

    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    public String getSignatureAlgorithm() {
        if (!StringUtils.isNullOrEmpty(signatureAlgorithm)) return signatureAlgorithm;
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

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    @Override
    public String toString() {
        return (
            "OptionalDTO{" +
            "hashAlgorithm='" +
            hashAlgorithm +
            '\'' +
            ", signatureAlgorithm='" +
            signatureAlgorithm +
            '\'' +
            ", returnInputData=" +
            returnInputData +
            '}'
        );
    }
}
