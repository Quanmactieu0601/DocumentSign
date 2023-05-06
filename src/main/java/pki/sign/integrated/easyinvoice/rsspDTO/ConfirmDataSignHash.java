package pki.sign.integrated.easyinvoice.rsspDTO;

public class ConfirmDataSignHash {

    private String pin;
    private String notiMsg;
    private String msgCaption;
    private String scaId;

    public ConfirmDataSignHash() {}

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
}
