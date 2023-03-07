package vn.easyca.signserver.pki.sign.integrated.easyinvoice.rsspDTO;

public class ConfirmDataSignHash {
    private String pin;
    private String notiMsg;
    private String msgCaption;
    private String scaId;
    private String rpDevice;
    private String rpOS;
    private String rpIP;
    private String rpMAC;

    public ConfirmDataSignHash() {
    }


    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getNotiMsg() {
        return notiMsg;
    }

    public void setNotiMsg(String notiMsg) {
        this.notiMsg = notiMsg;
    }

    public String getMsgCaption() {
        return msgCaption;
    }

    public void setMsgCaption(String msgCaption) {
        this.msgCaption = msgCaption;
    }

    public String getScaId() {
        return scaId;
    }

    public void setScaId(String scaId) {
        this.scaId = scaId;
    }

    public String getRpDevice() {
        return rpDevice;
    }

    public void setRpDevice(String rpDevice) {
        this.rpDevice = rpDevice;
    }

    public String getRpOS() {
        return rpOS;
    }

    public void setRpOS(String rpOS) {
        this.rpOS = rpOS;
    }

    public String getRpIP() {
        return rpIP;
    }

    public void setRpIP(String rpIP) {
        this.rpIP = rpIP;
    }

    public String getRpMAC() {
        return rpMAC;
    }

    public void setRpMAC(String rpMAC) {
        this.rpMAC = rpMAC;
    }
}
