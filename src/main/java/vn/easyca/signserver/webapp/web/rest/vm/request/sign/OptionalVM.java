package vn.easyca.signserver.webapp.web.rest.vm.request.sign;

public class OptionalVM {

    private String hashAlgorithm;

    private String signAlgorithm;

    private boolean returnInputData;

    private String otpCode;

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public String getSignAlgorithm() {
        return signAlgorithm;
    }

    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    public void setSignAlgorithm(String signAlgorithm) {
        this.signAlgorithm = signAlgorithm;
    }

    public boolean isReturnInputData() {
        return returnInputData;
    }

    public void setReturnInputData(boolean returnInputData) {
        this.returnInputData = returnInputData;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    @Override
    public String toString() {
        return "OptionalVM{" +
            "hashAlgorithm='" + hashAlgorithm + '\'' +
            ", signAlgorithm='" + signAlgorithm + '\'' +
            ", returnInputData=" + returnInputData +
            '}';
    }
}
