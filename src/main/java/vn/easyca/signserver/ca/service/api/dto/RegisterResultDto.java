package vn.easyca.signserver.ca.service.api.dto;

public class RegisterResultDto {
    private String activeCode;
    private String cert = null;
    private String certState;
    private int certId;
    private String certSerial = null;
    private String p12Data = null;
    private String p12Pass = null;
    private int status;
    private String message;

    public String getActiveCode() {
        return activeCode;
    }

    public String getCert() {
        return cert;
    }

    public String getCertState() {
        return certState;
    }

    public float getCertId() {
        return certId;
    }

    public String getCertSerial() {
        return certSerial;
    }

    public String getP12Data() {
        return p12Data;
    }

    public String getP12Pass() {
        return p12Pass;
    }

    public float getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public void setActiveCode(String activeCode) {
        this.activeCode = activeCode;
    }

    public void setCert(String cert) {
        this.cert = cert;
    }

    public void setCertState(String certState) {
        this.certState = certState;
    }

    public void setCertId(int certId) {
        this.certId = certId;
    }

    public void setCertSerial(String certSerial) {
        this.certSerial = certSerial;
    }

    public void setP12Data(String p12Data) {
        this.p12Data = p12Data;
    }

    public void setP12Pass(String p12Pass) {
        this.p12Pass = p12Pass;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
