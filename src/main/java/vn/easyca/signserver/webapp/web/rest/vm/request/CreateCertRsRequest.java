package vn.easyca.signserver.webapp.web.rest.vm.request;

import java.time.LocalDateTime;

public class CreateCertRsRequest {

    private String rawData;
    private String serial;
    private int signingCount;
    private String authMode;

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }


    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public int getSigningCount() {
        return signingCount;
    }

    public void setSigningCount(int signingCount) {
        this.signingCount = signingCount;
    }

    public String getAuthMode() {
        return authMode;
    }

    public void setAuthMode(String authMode) {
        this.authMode = authMode;
    }
}
