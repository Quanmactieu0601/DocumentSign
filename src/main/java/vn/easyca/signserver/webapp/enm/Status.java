package vn.easyca.signserver.webapp.enm;

public enum Status {
    FAIL(0 , false),
    SUCCESS(1, true);

    private int code ;
    private Boolean status ;

     Status(int code, Boolean status){
        this.code = code;
        this.status = status;
    }

    public static Status getStatusByCode(int code) {
        for (Status status : Status.values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
