package vn.easyca.signserver.webapp.service.dto;

import vn.easyca.signserver.webapp.enm.SignType;

public class OTPData {


    private SignType signType;
    private String OTPValue;
    private String authen;

    public SignType getSignType() {
        return signType;
    }

    public void setSignType(SignType signType) {
        this.signType = signType;
    }

    public String getOTPValue() {
        return OTPValue;
    }

    public void setOTPValue(String OTPValue) {
        this.OTPValue = OTPValue;
    }
}
