package vn.easyca.signserver.webapp.web.rest.vm.response;

public class P12CertificateRegisterResult {
    private String serial;
    private String pin;
    private String taxCode;
    private String identification;
    private int status;
    private String message;

    public String getSerial() { return serial; }

    public void setSerial(String serial) { this.serial = serial; }

    public String getPin() { return pin; }

    public void setPin(String pin) { this.pin = pin; }

    public String getTaxCode() { return taxCode; }

    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }

    public String getIdentification() { return identification; }

    public void setIdentification(String identification) { this.identification = identification; }

    public int getStatus() { return status; }

    public void setStatus(int status) { this.status = status; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }
}
