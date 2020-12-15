package vn.easyca.signserver.webapp.service.dto;

public class CertImportErrorDTO {
    private String p12FileName;
    private String message;

    public CertImportErrorDTO(String p12FileName, String message) {
        this.p12FileName = p12FileName;
        this.message = message;
    }

    public CertImportErrorDTO() {
    }

    public String getP12FileName() {
        return p12FileName;
    }

    public void setP12FileName(String p12FileName) {
        this.p12FileName = p12FileName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
